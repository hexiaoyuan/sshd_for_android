package me.xyhe.sshd4android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class Util {

    private static final String TAG = "sshd4android.Util";

    public static boolean mkdirs(String dirname) {
        File f = new File(dirname);
        return (f.mkdirs() || f.isDirectory());
    }

    public static String extractAssetToDir(Context ctt, String assertName,
                                           String outDir,
                                           String outFilename,
                                           boolean overwrite) {
        File parent_dir;

        if (outDir.startsWith(("/"))) {
            parent_dir = new File(outDir);
        } else {
            parent_dir = new File(ctt.getFilesDir().getParent() + File.separator + outDir);
        }

        if (!parent_dir.exists()) {
            parent_dir.mkdirs();
        }
        File outfile = new File(parent_dir, outFilename);

        if (outfile.exists()) {
            if (!overwrite)
                return outfile.toString();
            outfile.delete();
        }

        try {
            InputStream input = ctt.getAssets().open(assertName);
            FileOutputStream output = new FileOutputStream(outfile);

            try {
                byte[] tmp_data = new byte[4096];
                while (input.available() > 0) {
                    output.write(tmp_data, 0, input.read(tmp_data, 0, 4096));
                }
            } finally {
                output.close();
                input.close();
            }
        } catch (Exception ex) {
            Log.e(TAG, "extract assert error: ", ex);
            ex.printStackTrace();
        }
        return outfile.toString();
    }

    private static class StreamGobbler extends Thread {

        @SuppressWarnings("unused")
        private String mType;
        private InputStream mInStream;
        private OutputStream mOutStream;

        StreamGobbler(InputStream input, String type, OutputStream redirect) {
            this.mInStream = input;
            this.mOutStream = redirect;
        }

        @Override
        public void run() {
            InputStreamReader isr = null;
            BufferedReader br = null;
            PrintWriter pw = null;
            try {
                if (mOutStream != null) {
                    pw = new PrintWriter(mOutStream);
                }
                isr = new InputStreamReader(mInStream);
                br = new BufferedReader(isr, 512);
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (pw != null) {
                        pw.println(line);
                    }
                    Log.d(TAG, line);
                }

                if (pw != null) {
                    pw.flush();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (pw != null) {
                        pw.close();
                    }
                } catch (Exception ex) {
                }
                ;
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (Exception ex) {
                }
                ;
                try {
                    if (isr != null) {
                        isr.close();
                    }
                } catch (Exception ex) {
                }
                ;
                try {
                    if (mInStream != null) {
                        mInStream.close();
                    }
                } catch (Exception ex) {
                }
                try {
                    if (mOutStream != null) {
                        mOutStream.close();
                    }
                } catch (Exception ex) {
                }
            }
        }
    }

    public static int exec(String cmdline) {
        return exec(cmdline, null, null);
    }

    public static int exec(String cmdline, OutputStream outRedirect,
                           OutputStream errRedirect) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmdline);
            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(),
                    "ERROR", errRedirect);
            errorGobbler.start(); // kick off stderr

            StreamGobbler outGobbler = new StreamGobbler(p.getInputStream(),
                    "STDOUT", outRedirect);
            outGobbler.start(); // kick off stdout

            int exitVal = p.waitFor();
            return exitVal;
        } catch (Throwable t) {
            t.printStackTrace();
            return -1;
        } finally {
            try {
                if (p != null) {
                    p.destroy();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * exec_out: 运行命令，并返回stdout的内容. NOTE: 请确保该运行的命令行不会有stderr,
     * 否则请使用Util.exec函数代替, 本函数的存在只是为了 提供一个轻量级的exec而已。
     *
     * @param cmdline
     * @return
     */
    public static String exec_out(String cmdline) {

        ArrayList<String> tmp = new ArrayList<String>();
        exec_out(cmdline, tmp);
        StringBuffer sb = new StringBuffer();
        for (String s : tmp) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * exec_out: 运行命令，并返回stdout的内容. NOTE: 请确保该运行的命令行不会有stderr,
     * 否则请使用Util.exec函数代替, 本函数的存在只是为了 提供一个轻量级的exec而已。
     *
     * @param cmdline
     * @param outlines
     * @return
     */
    public static int exec_out(String cmdline, ArrayList<String> outlines) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        InputStream stdout = null;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmdline);
            stdout = p.getInputStream();
            isr = new InputStreamReader(stdout);
            br = new BufferedReader(isr, 1024);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (outlines != null)
                    outlines.add(line);
            }
            int exitVal = p.waitFor();
            return exitVal;
        } catch (Throwable t) {
            t.printStackTrace();
            return -1;
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (Exception ex) {
            }
            try {
                if (isr != null)
                    isr.close();
            } catch (Exception ex) {
            }
            try {
                if (stdout != null)
                    stdout.close();
            } catch (Exception ex) {
            }
            try {
                p.destroy();
            } catch (Exception ex) {
            }
        }
    }

}
