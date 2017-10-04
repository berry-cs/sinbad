package core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import core.log.Errors;

/**
 * Takes care of low-level details of actually producing an input stream
 * from a file or URL with utility methods for IO. Some methods have been
 * cannibalized from the Processing project. 
 */
public class FileLoader {

    private String zipFileEntry;   // controls which entry is loaded from a zip file
    private boolean isZip;

    public FileLoader() {
        this.zipFileEntry = null;
        this.isZip = false;
    }
    
    public void setZipFileEntry(String zipFileEntry) {
        this.zipFileEntry = zipFileEntry;
    }
    
    public void setZip(boolean isZip) {
        this.isZip = isZip;
    }
    
    

    /**
     * Ripped from processing/core/PApplet.java (http://processing.org)
     *
     * This is a function for advanced programmers to open a Java InputStream.
     * It's useful if you want to use the facilities provided by PApplet to
     * easily open files from the data folder or from a URL, but want an
     * InputStream object so that you can use other parts of Java to take more
     * control of how the stream is read.<br />
     * <br />
     * The filename passed in can be:<br />
     * - A URL, for instance <b>openStream("http://processing.org/")</b><br />
     * - A file in the sketch's <b>data</b> folder<br />
     * - The full path to a file to be opened locally (when running as an
     * application)<br />
     * <br />
     * If the requested item doesn't exist, null is returned. If not online,
     * this will also check to see if the user is asking for a file whose name
     * isn't properly capitalized. If capitalization is different, an error
     * will be printed to the console. This helps prevent issues that appear
     * when a sketch is exported to the web, where case sensitivity matters, as
     * opposed to running from inside the Processing Development Environment on
     * Windows or Mac OS, where case sensitivity is preserved but ignored.<br />
     * <br />
     * If the file ends with <b>.gz</b>, the stream will automatically be gzip
     * decompressed. If you don't want the automatic decompression, use the
     * related function <b>createInputRaw()</b>.
     * <br />
     * In earlier releases, this function was called <b>openStream()</b>.<br />
     * <br />
     *
     * ( end auto-generated )
     *
     * <h3>Advanced</h3>
     * Simplified method to open a Java InputStream.
     * <p>
     * This method is useful if you want to use the facilities provided
     * by PApplet to easily open things from the data folder or from a URL,
     * but want an InputStream object so that you can use other Java
     * methods to take more control of how the stream is read.
     * <p>
     * If the requested item doesn't exist, null is returned.
     * (Prior to 0096, die() would be called, killing the applet)
     * <p>
     * For 0096+, the "data" folder is exported intact with subfolders,
     * and openStream() properly handles subdirectories from the data folder
     * <p>
     * If not online, this will also check to see if the user is asking
     * for a file whose name isn't properly capitalized. This helps prevent
     * issues when a sketch is exported to the web, where case sensitivity
     * matters, as opposed to Windows and the Mac OS default where
     * case sensitivity is preserved but ignored.
     * <p>
     * It is strongly recommended that libraries use this method to open
     * data files, so that the loading sequence is handled in the same way
     * as functions like loadBytes(), loadImage(), etc.
     * <p>
     * The filename passed in can be:
     * <UL>
     * <LI>A URL, for instance openStream("http://processing.org/");
     * <LI>A file in the sketch's data folder
     * <LI>Another file to be opened locally (when running as an application)
     * </UL>
     *
     * @param path the path (URL/local filename path) of the file to use as input
     *
     */

    private static class InputInfo {
        public InputInfo(InputStream input, String path) {
            this.input = input;
            this.path = path;
        }
        InputStream input;
        String path; 
    }
    
    public InputStream createInput(String path) {
        //System.err.println("IOUtil::createInput(" + path + ")");

        InputInfo inputInfo = createInputRaw(path);
        if (inputInfo == null) {
            return null;
        }
            
        InputStream input = inputInfo.input;
        String newpath = inputInfo.path;

        final String lower = newpath.toLowerCase();
        if (input != null) {
            if (lower.endsWith(".gz")) {
                try {
                    return new GZIPInputStream(input);
                } catch (IOException e) {
                    //e.printStackTrace();
                    return null;
                }
            } else if (lower.endsWith(".zip") || lower.contains(".zip?")) {
                try {
                    Thread t = null;
                    if (newpath.contains("://")) {
                        t = new Thread(new DotPrinter(String.format("Reading zip file: %s (this may take a moment)", newpath)));
                        t.start();
                    }
                    
                    try {
                        ZipInputStream zin = new ZipInputStream(input);
                        ZipEntry ze;
                        String dirlist = "";
                        
                        if (this.zipFileEntry == null) {   // no file entry option was specified
                            int count = 0;
                            while ( (ze = zin.getNextEntry()) != null ) {
                                count++;
                                dirlist = dirlist + "   " + ze.getName() + "\n";
                            }
                            if (count == 1) { // try to reopen
                                zin = new ZipInputStream(createInputRaw(path).input);
                                zin.getNextEntry();
                                return zin;
                            }
                            
                            throw Errors.exception(DataIOException.class, "io:zipentry", newpath, dirlist);
                        } else {
                            while ( (ze = zin.getNextEntry()) != null ) {
                                if (ze.getName().equals(this.zipFileEntry)) {
                                    return zin;
                                }
                            }
                            throw Errors.exception(DataIOException.class, "io:nozipentry", this.zipFileEntry, newpath);
                        }
                    } finally {
                        if (t != null) {
                            t.interrupt();
                        }
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    return null;
                }
            }
        }
        return input;
    }


    /**
     * Ripped from processing/core/PApplet.java (http://processing.org)
     * 
     * Call openStream() without automatic gzip decompression.
     */
    private static InputInfo createInputRaw(String filename) {
        if (filename == null) return null;

        if (filename.length() == 0) {
            // an error will be called by the parent function
            //System.err.println("The filename passed to openStream() was empty.");
            return null;
        }

        // First check whether this looks like a URL. This will prevent online
        // access logs from being spammed with GET /sketchfolder/http://blahblah
        if (filename.contains("://")) {  // at least smells like URL
            try {
                URL url = new URL(filename);
                
                URLConnection conn = url.openConnection();
                conn.addRequestProperty("User-Agent", "Mozilla/4.76"); 
                if (conn instanceof HttpURLConnection) {
                    HttpURLConnection httpConn = (HttpURLConnection) conn;
                    // Will not handle a protocol change (see below)
                    httpConn.setInstanceFollowRedirects(true);
                    int response = httpConn.getResponseCode();
                    // Normally will not follow HTTPS redirects from HTTP due to security concerns
                    // http://stackoverflow.com/questions/1884230/java-doesnt-follow-redirect-in-urlconnection/1884427
                    if (response >= 300 && response < 400) {
                        String newLocation = httpConn.getHeaderField("Location");
                        return createInputRaw(newLocation);
                    }
                    
                    Map<String, List<String>> map = httpConn.getHeaderFields();
                    //System.out.println(map);
                    if (map.containsKey("Content-Disposition")) {
                        List<String> vals = map.get("Content-Disposition");
                        for (String f : vals) {
                            if (f.startsWith("attachment") && f.contains("filename=")) {
                                filename = f.substring(f.indexOf("filename=") + 9).replace("\"", "");
                            }
                        }
                    }
                    
                    return new InputInfo(conn.getInputStream(), filename);
                } else if (conn instanceof JarURLConnection) {
                    return new InputInfo(url.openStream(), filename);
                }
            } catch (MalformedURLException | UnknownHostException ue) {
                // not a url, that's fine

            } catch (FileNotFoundException fnfe) {
                // Added in 0119 b/c Java 1.5 throws FNFE when URL not available.
                // http://dev.processing.org/bugs/show_bug.cgi?id=403

            } catch (IOException e) {
                // changed for 0117, shouldn't be throwing exception
                e.printStackTrace();
                //System.err.println("Error downloading from URL " + filename);
                return null;
                //throw new RuntimeException("Error downloading from URL " + filename);
            }
        }

        InputStream stream = null;

        // Moved this earlier than the getResourceAsStream() checks, because
        // calling getResourceAsStream() on a directory lists its contents.
        // http://dev.processing.org/bugs/show_bug.cgi?id=716
        try {
            // First see if it's in a data folder. This may fail by throwing
            // a SecurityException. If so, this whole block will be skipped.
            File file = new File(ProcessingDetector.tryToFixPath(filename));    // .nah.

            if (file.isDirectory()) {
                return null;
            }
            if (file.exists()) {
                try {
                    // handle case sensitivity check
                    String filePath = file.getCanonicalPath();
                    String filenameActual = new File(filePath).getName();
                    // make sure there isn't a subfolder prepended to the name
                    String filenameShort = new File(filename).getName();
                    // if the actual filename is the same, but capitalized
                    // differently, warn the user.
                    //if (filenameActual.equalsIgnoreCase(filenameShort) &&
                    //!filenameActual.equals(filenameShort)) {
                    if (!filenameActual.equals(filenameShort)) {
                        throw new RuntimeException("This file is named " +
                                filenameActual + " not " +
                                filename + ". Rename the file " +
                                "or change your code.");
                    }
                } catch (IOException e) { }
            }

            // if this file is ok, may as well just load it
            stream = new FileInputStream(file);
            if (stream != null) return new InputInfo(stream, ProcessingDetector.tryToFixPath(filename));

            // have to break these out because a general Exception might
            // catch the RuntimeException being thrown above
        } catch (IOException ioe) {
        } catch (SecurityException se) { }

        // Using getClassLoader() prevents java from converting dots
        // to slashes or requiring a slash at the beginning.
        // (a slash as a prefix means that it'll load from the root of
        // the jar, rather than trying to dig into the package location)
        if (ProcessingDetector.pappletClass != null) {
            ClassLoader cl = ProcessingDetector.pappletClass.getClassLoader();
            // by default, data files are exported to the root path of the jar.
            // (not the data folder) so check there first.
            stream = cl.getResourceAsStream("data/" + filename);
            if (stream != null) {
                String cn = stream.getClass().getName();
                // this is an irritation of sun's java plug-in, which will return
                // a non-null stream for an object that doesn't exist. like all good
                // things, this is probably introduced in java 1.5. awesome!
                // http://dev.processing.org/bugs/show_bug.cgi?id=359
                if (!cn.equals("sun.plugin.cache.EmptyInputStream")) {
                    return new InputInfo(stream, "data/" + filename);
                }
            }

            // When used with an online script, also need to check without the
            // data folder, in case it's not in a subfolder called 'data'.
            // http://dev.processing.org/bugs/show_bug.cgi?id=389
            stream = cl.getResourceAsStream(filename);
            if (stream != null) {
                String cn = stream.getClass().getName();
                if (!cn.equals("sun.plugin.cache.EmptyInputStream")) {
                    return new InputInfo(stream, filename);
                }
            }
        }


        try {
            // attempt to load from a local file, used when running as
            // an application, or as a signed applet
            try {  // first try to catch any security exceptions
                try {
                    stream = new FileInputStream(ProcessingDetector.tryToFixPath(filename));
                    if (stream != null) return new InputInfo(stream, ProcessingDetector.tryToFixPath(filename));
                } catch (IOException e2) { }

                try {
                    stream = new FileInputStream(filename);
                    if (stream != null) return new InputInfo(stream, filename);
                } catch (IOException e1) { }

            } catch (SecurityException se) { }  // online, whups

        } catch (Exception e) {
            //die(e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    public String getZipFileEntry() {
        return this.zipFileEntry;
    }

    
}
