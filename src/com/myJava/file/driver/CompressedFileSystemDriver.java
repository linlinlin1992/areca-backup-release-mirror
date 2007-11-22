package com.myJava.file.driver;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.myJava.file.CompressionArguments;
import com.myJava.file.archive.zip64.ZipEntry;
import com.myJava.file.archive.zip64.ZipInputStream;
import com.myJava.file.archive.zip64.ZipOutputStream;
import com.myJava.file.archive.zip64.ZipVolumeStrategy;
import com.myJava.file.attributes.Attributes;
import com.myJava.file.multivolumes.VolumeInputStream;
import com.myJava.object.EqualsHelper;
import com.myJava.object.HashHelper;
import com.myJava.object.ToStringHelper;

/**
 * Driver "chainable" apportant des fonctionnalit�s de compression.
 * <BR>
 * @author Olivier PETRUCCI
 * <BR>
 * <BR>Areca Build ID : 2156529904998511409
 */
 
 /*
 Copyright 2005-2007, Olivier PETRUCCI.
 
This file is part of Areca.

    Areca is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Areca is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areca; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
public class CompressedFileSystemDriver 
extends AbstractLinkableFileSystemDriver {

    private CompressionArguments compression = new CompressionArguments();
    private File root;
    private static final String SUFFIX = ".zip";
    
    /**
     * @param directoryRoot
     * @param key
     */
    public CompressedFileSystemDriver(
            File root,
            FileSystemDriver predecessor, 
            CompressionArguments compression) {
        super();
        this.root = root;
        this.compression = compression;
        this.setPredecessor(predecessor);
    }
    
    public boolean canRead(File file) {
        return this.predecessor.canRead(encode(file));
    }
    
    public boolean canWrite(File file) {
        return this.predecessor.canWrite(encode(file));
    }
    
    public boolean createNewFile(File file) throws IOException {
        return this.predecessor.createNewFile(encode(file));
    }
    
    public boolean delete(File file) {
        File[] f = resolveFiles(file);
        boolean bool = true;
        for (int i=0; i<f.length; i++) {
            if (! predecessor.delete(f[i])) {
                bool = false;
                break;
            }
        }
        return bool;
    }
    
    public boolean exists(File file) {
        return this.predecessor.exists(encode(file));
    }

    public FileInformations getInformations(File file) {
        File[] f = resolveFiles(file);
        boolean bool = true;
        FileInformations fi = null;
        long length = -1;
        for (int i=0; i<f.length; i++) {
            fi = predecessor.getInformations(f[i]);
            if (fi.isLengthSet()) {
                length += fi.getLength();
            }
        }
        if (fi != null && length != -1) {
            fi.enforceLength(length);
        }
        return fi;
    }
    
    public boolean isDirectory(File file) {
        return this.predecessor.isDirectory(encode(file));
    }
    
    public boolean isFile(File file) {
        return this.predecessor.isFile(encode(file));
    }

    public boolean isHidden(File file) {
        return this.predecessor.isHidden(encode(file));
    }
    
    public long lastModified(File file) {
        File[] f = resolveFiles(file);
        long time = 0;
        for (int i=0; i<f.length; i++) {
            time = Math.max(time, predecessor.lastModified(f[i]));
        }
        return time;
    }
    
    public long length(File file) {
        File[] f = resolveFiles(file);
        long length = 0;
        for (int i=0; i<f.length; i++) {
            length += predecessor.length(f[i]);
        }
        return length;
    }
    
    public String[] list(File file, FilenameFilter filter) {
        File[] files = this.listFiles(file, filter);
        if (files != null) {
            String[] ret = new String[files.length];
            for (int i=0; i<files.length; i++) {
                ret[i] = predecessor.getAbsolutePath(files[i]);
            }
            
            return ret;
        } else {
            return null;
        }
    }
    
    public String[] list(File file) {
        File[] files = this.listFiles(file);
        if (files != null) {
            String[] ret = new String[files.length];
            for (int i=0; i<files.length; i++) {
                ret[i] = predecessor.getAbsolutePath(files[i]);
            }
            
            return ret;
        } else {
            return null;
        }
    }
    
    private File[] processFiles(File[] files) {
        if (files != null) {
            ArrayList list = new ArrayList();
            for (int i=0; i<files.length; i++) {
                if (files[i].getName().endsWith(SUFFIX)) {
                    list.add(this.decode(files[i]));
                }
            }
            return (File[])list.toArray(new File[list.size()]);
        } else {
            return null;
        }
    }
    
    public File[] listFiles(File file, FileFilter filter) {
        File[] files = this.predecessor.listFiles(this.encode(file), new FileFilterAdapter(filter, this));        
        return processFiles(files);
    }
    
    public File[] listFiles(File file, FilenameFilter filter) {
        File[] files = this.predecessor.listFiles(this.encode(file), new FilenameFilterAdapter(filter, this));
        return processFiles(files);
    }
    
    public File[] listFiles(File file) {
        File[] files = this.predecessor.listFiles(this.encode(file));
        return processFiles(files);
    }
    
    public boolean mkdir(File file) {
        return this.predecessor.mkdir(encode(file));
    }
    
    public boolean mkdirs(File file) {
        return this.predecessor.mkdirs(encode(file));
    }
    
    public boolean renameTo(File source, File dest) {
        File[] f = resolveFiles(source);
        boolean bool = true;
        
        File target = new File(encode(predecessor.getParentFile(dest)), predecessor.getName(dest));
        ZipVolumeStrategy vol = new ZipVolumeStrategy(target);
        for (int i=0; i<f.length; i++) {
            File encodedDest;
            if (i == f.length - 1) {
                encodedDest = vol.getFinalArchive();
            } else {
                encodedDest = vol.getNextFile();
            }
            
            if (! predecessor.renameTo(f[i], encodedDest)) {
                bool = false;
                break;
            }
        }
        return bool;
    }
    
    public boolean setLastModified(File file, long time) {
        File[] f = resolveFiles(file);
        boolean bool = true;
        for (int i=0; i<f.length; i++) {
            if (! predecessor.setLastModified(f[i], time)) {
                bool = false;
                break;
            }
        }
        return bool;
    }
    
    public boolean setReadOnly(File file) {
        File[] f = resolveFiles(file);
        boolean bool = true;
        for (int i=0; i<f.length; i++) {
            if (! predecessor.setReadOnly(f[i])) {
                bool = false;
                break;
            }
        }
        return bool;
    }
    
    public InputStream getFileInputStream(File file) throws IOException {
        ZipInputStream zin;
        if (compression.isMultiVolumes()) {
            File target = new File(encode(predecessor.getParentFile(file)), predecessor.getName(file));
            ZipVolumeStrategy strategy = new ZipVolumeStrategy(target, predecessor, false);
            zin = new ZipInputStream(new VolumeInputStream(strategy));
        } else {
            zin = new ZipInputStream(predecessor.getFileInputStream(encode(file)));
        }
        if (compression.getCharset() != null) {
            zin.setCharset(compression.getCharset());
        }
        zin.getNextEntry();
        return zin;
    }
    
    public OutputStream getCachedFileOutputStream(File file) throws IOException {
        return getOutputStream(file, true);
    }    
    
    public OutputStream getFileOutputStream(File file) throws IOException {
        return getOutputStream(file, false);
    }    
    
    private OutputStream getOutputStream(File file, boolean cached) throws IOException {
        ZipOutputStream zout;
        if (compression.isMultiVolumes()) {
            File target = new File(encode(predecessor.getParentFile(file)), predecessor.getName(file));
            ZipVolumeStrategy strategy = new ZipVolumeStrategy(target, predecessor, cached);
            zout = new ZipOutputStream(strategy, compression.getVolumeSize() * 1024 * 1024, compression.isUseZip64());
        } else {
            zout = new ZipOutputStream(predecessor.getFileOutputStream(encode(file)), compression.isUseZip64());
        }
        if (compression.getCharset() != null) {
            zout.setCharset(compression.getCharset());
        }
        if (compression.getComment() != null) {
            zout.setComment(compression.getComment());
        }
        zout.putNextEntry(new ZipEntry(file.getName()));
        return zout;
    }    
    
    public OutputStream getFileOutputStream(File file, boolean append) throws IOException {
        if (append) {
            throw new IllegalArgumentException("Cannot open an OutputStream in 'append' mode on a compressed FileSystem");
        }
        return getFileOutputStream(file);
    }   

    public void deleteOnExit(File file) {
        File[] f = resolveFiles(file);
        for (int i=0; i<f.length; i++) {
            predecessor.deleteOnExit(f[i]);
        }
    }
    
    public Attributes getAttributes(File file) throws IOException {
        return this.predecessor.getAttributes(encode(file));
    }

    public void applyAttributes(Attributes p, File file) throws IOException {
        File[] f = resolveFiles(file);
        for (int i=0; i<f.length; i++) {
            predecessor.applyAttributes(p, f[i]);
        }
    }
    
    public int hashCode() {
        int h = HashHelper.initHash(this);
        h = HashHelper.hash(h, this.predecessor);
        
        return h;
    }
    
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof CompressedFileSystemDriver) {
            CompressedFileSystemDriver other = (CompressedFileSystemDriver)o;
            
            return (
                    EqualsHelper.equals(other.predecessor, this.predecessor) 
            );
        } else {
            return false;
        }
    }
    
    public String toString() {
        StringBuffer sb = ToStringHelper.init(this);
        ToStringHelper.append("Predecessor", this.predecessor, sb);
        return ToStringHelper.close(sb);
    }

    public boolean directFileAccessSupported() {
        return false;
    }
    
    public boolean isContentSensitive() {
        return true;
    }
    
    protected File encode(File file) {
        File orig = file.getAbsoluteFile();
        if (orig.equals(this.root)) {
            return orig;
        } else {
            return new File(this.encode(orig.getParentFile()), this.encode(orig.getName()));
        }
    }
    
    protected File[] resolveFiles(File file) {
        File orig = file.getAbsoluteFile();
        if (orig.equals(this.root)) {
            return new File[] {orig};
        } else {
            if (compression.isMultiVolumes()) {
                File target = new File(encode(predecessor.getParentFile(orig)), predecessor.getName(orig));
                ZipVolumeStrategy vol = new ZipVolumeStrategy(target);
                ArrayList list = new ArrayList(1);
                while (true) {
                    File f = vol.getNextFile();
                    if (predecessor.exists(f)) {
                        list.add(f);
                    } else {
                        break;
                    }
                }
                list.add(vol.getFinalArchive());
                return (File[])list.toArray(new File[list.size()]);
            } else {
                return new File[] {new File(this.encode(orig.getParentFile()), this.encode(orig.getName()))};
            }
        }
    }
    
    protected File decode(File file) {
        File orig = file.getAbsoluteFile();
        if (orig.equals(this.root)) {
            return orig;
        } else {
            return new File(this.decode(orig.getParentFile()), this.decode(orig.getName()));
        }
    }
    
    private String encode(String name) {
        return name + SUFFIX;
    }
    
    private String decode(String name) {
        if (! name.endsWith(SUFFIX)) {
            throw new IllegalArgumentException("Illegal file name : " + name + ". It is expected to end with '" + SUFFIX + "'");
        }
        return name.substring(0, name.length() - SUFFIX.length());
    }
    
    protected static class FilenameFilterAdapter implements FilenameFilter {
        protected FilenameFilter filter;
        protected CompressedFileSystemDriver driver;
        
        public FilenameFilterAdapter(
                FilenameFilter wrappedFilter,
                CompressedFileSystemDriver driver) {
            this.filter = wrappedFilter;
            this.driver = driver;
        }

        public boolean accept(File dir, String name) {
            File targetDirectory = driver.decode(dir);
            String targetName = driver.decode(name);
            return filter.accept(targetDirectory, targetName);
        }
        
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (! (obj instanceof FilenameFilterAdapter)) {
                return false;
            } else {
                FilenameFilterAdapter other = (FilenameFilterAdapter)obj;
                return 
                    EqualsHelper.equals(this.filter, other.filter)
                    && EqualsHelper.equals(this.driver, other.driver);
            }
        }

        public int hashCode() {
            int h = HashHelper.initHash(this);
            h = HashHelper.hash(h, filter);
            h = HashHelper.hash(h, driver);
            return h;
        }

        public String toString() {
            StringBuffer sb = ToStringHelper.init(this);
            ToStringHelper.append("Filter", this.filter, sb);
            ToStringHelper.append("Driver", this.driver, sb);
            return ToStringHelper.close(sb);
        }
    }
    
    protected static class FileFilterAdapter implements FileFilter {
        protected FileFilter filter;
        protected CompressedFileSystemDriver driver;
        
        public FileFilterAdapter(
                FileFilter wrappedFilter,
                CompressedFileSystemDriver driver) {
            this.filter = wrappedFilter;
            this.driver = driver;
        }

        public boolean accept(File filename) {
            File target = driver.decode(filename);
            return filter.accept(target);
        }
        
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (! (obj instanceof FileFilterAdapter)) {
                return false;
            } else {
                FileFilterAdapter other = (FileFilterAdapter)obj;
                return 
                    EqualsHelper.equals(this.filter, other.filter)
                    && EqualsHelper.equals(this.driver, other.driver);
            }
        }

        public int hashCode() {
            int h = HashHelper.initHash(this);
            h = HashHelper.hash(h, filter);
            h = HashHelper.hash(h, driver);
            return h;
        }

        public String toString() {
            StringBuffer sb = ToStringHelper.init(this);
            ToStringHelper.append("Filter", this.filter, sb);
            ToStringHelper.append("Driver", this.driver, sb);
            return ToStringHelper.close(sb);
        }
    }
}