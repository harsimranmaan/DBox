/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package dBox.Client;

import dBox.utils.CustomLogger;
import dBox.utils.Hashing;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */
public class WatchDir extends Thread
{

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
//    private ConfigManager config;
    private boolean trace = false;
//    private HashMap<Path, String> fileCurrentHash;
    private HashMap<Path, String> fileEvent;
    private Path basePath;
    private ArrayList<Path> ignorePath;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event)
    {
        return (WatchEvent<T>) event;
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    WatchDir(Path dir, boolean recursive, HashMap<Path, String> fileHash, HashMap<Path, String> fileEvent, ArrayList<Path> ignorePath) throws IOException
    {
        this.basePath = dir;
        //      this.fileCurrentHash = fileHash;
        this.fileEvent = fileEvent;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.ignorePath = ignorePath;
        this.recursive = recursive;
        if (recursive)
        {
            CustomLogger.log("Scanning dir " + dir + " ...");
            registerAll(dir);

            CustomLogger.log("Done");
        }
        else
        {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException
    {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace)
        {
            Path prev = keys.get(key);
            if (prev == null)
            {
                System.out.format("register: %s\n", dir);
            }
            else
            {
                if (!dir.equals(prev))
                {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException
    {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents()
    {
        for (;;)
        {
            // wait for key to be signalled
            WatchKey key;
            try
            {
                key = watcher.take();
            }
            catch (InterruptedException x)
            {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null)
            {
                CustomLogger.log("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents())
            {
                WatchEvent.Kind kind = event.kind();

                //ignore overflow
                if (kind == OVERFLOW)
                {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE))
                {
                    try
                    {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS))
                        {
                            registerAll(child);
                            //Check if the new directory has any file for copy
                            touchAllFiles(child);
                        }
                    }
                    catch (IOException x)
                    {
                        // ignore to keep sample readbale
                    }
                }
                // print out event
                CustomLogger.log("Event " + event.kind().name() + " File " + name + " Path " + child);
                //ignore certain paths
                if (!ignorePath.contains(child))
                {
                    //Log the event for processing
                    fileEvent.put(child, kind.name());
                }

                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid)
                {
                    keys.remove(key);

                    // all directories are inaccessible
                    if (keys.isEmpty())
                    {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Gets the path of all the files in directory/sub-directory path
     * <p/>
     * @param path
     */
    private void touchAllFiles(Path path)
    {

        File folder = new File(path.toString());
        File[] filelist = folder.listFiles();
        Path filepath;
        for (int i = 0; i < filelist.length; i++)
        {
            filepath = filelist[i].toPath();
            if (Files.isDirectory(filepath, NOFOLLOW_LINKS))
            {
                touchAllFiles(filepath);
            }
            else
            {

                //ignore certain paths
                if (ignorePath.contains(filepath))
                {
                    continue;
                }
                //Mark any offline changes
                fileEvent.put(filepath, "ENTRY_MODIFY");
            }
        }
    }

    @Override
    public void run()
    {
        touchAllFiles(basePath);
        //       CustomLogger.log(fileCurrentHash.toString());
        this.processEvents();
    }
}
