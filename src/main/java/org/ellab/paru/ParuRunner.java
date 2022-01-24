package org.ellab.paru;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.ellab.paru.Paru.ParuListener;
import org.ellab.paru.filehandler.FileHandler;
import org.ellab.paru.filehandler.FileHandlerFactory;
import org.ellab.paru.filehandler.FileNotEncryptedException;
import org.ellab.paru.filehandler.InvalidFileFormatException;
import org.ellab.paru.pattern.CharPattern;
import org.ellab.paru.pattern.PasswordPattern;

public class ParuRunner {
    // private static final int cores = System.getProperty(ParuRunner.class.getName() + ".cores") != null
    // && System.getProperty(ParuRunner.class.getName() + ".cores").length() > 0 ? Integer.valueOf(System
    // .getProperty(ParuRunner.class.getName() + ".cores")) : Runtime.getRuntime().availableProcessors();
    private static final int cores = 1;

    private Runner[] runners;
    private ParuListener callback;
    private Performance perf;
    private int progressInterval;
    private long total;
    private AtomicInteger count = new AtomicInteger();

    private static class Runner {
        private ParuRunner pr;
        private FileHandler fh;
        private List<PasswordPattern> pp = new ArrayList<PasswordPattern>();

        private boolean stop;
        private boolean running;

        private Runner(ParuRunner pr, FileHandler fh, List<PasswordPattern> pp) {
            this.pr = pr;
            this.fh = fh;
            this.pp.addAll(pp);
        }

        private void runAllPatterns() {
            try {
                for (PasswordPattern p : pp) {
                    while (p.hasNext()) {
                        String pattern = p.next();
                        if (pattern == null) {
                            break;
                        }

                        if (stop) {
                            running = false;
                            pr.stopped();
                            return;
                        }
                        while (!running) {
                            try {
                                Thread.sleep(50);
                            }
                            catch (InterruptedException ex) {
                            }
                        }

                        boolean success = fh.verify(pattern);

                        if (success) {
                            running = false;
                            pr.result(pattern);
                            return;
                        }
                        else {
                            pr.progress(pattern);
                        }
                    }
                }
                running = false;
                pr.noresult();
            }
            catch (Exception ex) {
                pr.error("Unexpected exception", ex);
            }
        }

        private void start() {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    running = true;
                    runAllPatterns();
                    running = false;
                    fh.close();
                }
            });

            running = true;
            thread.start();
        }

        public boolean isRunning() {
            return running;
        }

        public void pause() {
            running = false;
        }

        public void resume() {
            running = true;
        }

        public void stop() {
            stop = true;
            running = false;
        }
    }

    public static ParuRunner create(String filename, ParuListener listener, int progressInterval, PasswordPattern p)
            throws IOException {
        List<PasswordPattern> pp = new ArrayList<PasswordPattern>();
        pp.add(p);
        return create(filename, listener, progressInterval, pp);
    }

    public static ParuRunner create(String filename, ParuListener listener, int progressInterval,
            List<PasswordPattern> pp) throws IOException {

        ParuRunner pr = new ParuRunner(listener, progressInterval);

        for (PasswordPattern p : pp) {
            pr.total += (long) p.total();
        }

        int threads = Math.min(cores, pp.size());
        ArrayList<List<PasswordPattern>> threadPp = new ArrayList<List<PasswordPattern>>();
        for (int i = 0; i < threads; i++) {
            threadPp.add(new ArrayList<PasswordPattern>());
        }
        int i = 0;
        for (PasswordPattern p : pp) {
            threadPp.get(i).add(p);
            i = (i + 1) % threads;
        }

        pr.runners = new Runner[threads];
        for (i = 0; i < threads; i++) {
            pr.runners[i] = new Runner(pr, createFileHandler(filename, listener), threadPp.get(i));
        }

        pr.callback.combinations(pr.total);

        return pr;
    }

    public static ParuRunner createFromCharPattern(String filename, ParuListener listener, int progressInterval,
            String chars, int minLen, int maxLen, String first, String last, String prefix, String suffix)
            throws IOException {

        String[] prefixs = (prefix == null ? "" : prefix).split("\\,");
        String[] suffixs = (suffix == null ? "" : suffix).split("\\,");

        if (cores > 1 && prefixs.length > 1) {
            int threads = Math.min(cores, prefixs.length);
            ArrayList<ArrayList<String>> prefixList = new ArrayList<ArrayList<String>>();
            for (int i = 0; i < threads; i++) {
                prefixList.add(new ArrayList<String>());
            }
            int i = 0;
            for (String s : prefixs) {
                prefixList.get(i).add(s);
                i = (i + 1) % threads;
            }
            List<PasswordPattern> pp = new ArrayList<PasswordPattern>();
            for (i = 0; i < threads; i++) {
                pp.add(new CharPattern(chars, minLen, maxLen, first, last, prefixList.get(i).toArray(new String[0]),
                        suffixs));
            }

            return create(filename, listener, progressInterval, pp);
        }
        if (cores > 1 && suffixs.length > 1) {
            int threads = Math.min(cores, suffixs.length);
            ArrayList<ArrayList<String>> suffixList = new ArrayList<ArrayList<String>>();
            for (int i = 0; i < threads; i++) {
                suffixList.add(new ArrayList<String>());
            }
            int i = 0;
            for (String s : suffixs) {
                suffixList.get(i).add(s);
                i = (i + 1) % threads;
            }
            List<PasswordPattern> pp = new ArrayList<PasswordPattern>();
            for (i = 0; i < threads; i++) {
                pp.add(new CharPattern(chars, minLen, maxLen, first, last, prefixs, suffixList.get(i).toArray(
                        new String[0])));
            }

            return create(filename, listener, progressInterval, pp);
        }
        else if (cores > 1) {
            long total = 0;
            int threads = cores;
            ArrayList<ArrayList<PasswordPattern>> pp = new ArrayList<ArrayList<PasswordPattern>>();
            for (int i = 0; i < threads; i++) {
                pp.add(new ArrayList<PasswordPattern>());
            }
            CharPattern dummy = new CharPattern(chars, minLen, maxLen, first, last, prefixs, suffixs);
            for (int i = minLen; i <= maxLen; i++) {
                long firstSerial = dummy.getFirstSerial(i);
                long lastSerial = dummy.getLastSerial(i);
                long s, e = 0;
                for (int j = 0; j < threads; j++) {
                    if (j == 0) {
                        s = firstSerial;
                    }
                    else {
                        s = e + 1;
                    }
                    if (j == threads - 1) {
                        e = lastSerial;
                    }
                    else {
                        e = s + (lastSerial - firstSerial) / threads;
                    }
                    CharPattern cp = new CharPattern(chars, i, s, e, prefixs, suffixs);
                    total += (long) cp.total();
                    pp.get(j).add(cp);
                }
            }

            ParuRunner pr = new ParuRunner(listener, progressInterval);
            pr.total = total;

            pr.runners = new Runner[threads];
            for (int i = 0; i < threads; i++) {
                pr.runners[i] = new Runner(pr, createFileHandler(filename, listener), pp.get(i));
            }

            pr.callback.combinations(pr.total);

            return pr;
        }
        else {
            return create(filename, listener, progressInterval, new CharPattern(chars, minLen, maxLen, first, last,
                    prefix, suffix));
        }
    }

    private ParuRunner(ParuListener callback, int progressInterval) {
        this.callback = callback;
        this.progressInterval = progressInterval;
    }

    private static FileHandler createFileHandler(String filename, ParuListener callback) throws IOException {
        try {
            return FileHandlerFactory.getFileHandler(filename);
        }
        catch (FileNotFoundException ex) {
            callback.error(Constants.MSG_FILE_NOT_FOUND, null);
        }
        catch (InvalidFileFormatException ex) {
            callback.error(Constants.MSG_INVALID_FILE_FORMAT, null);
        }
        catch (FileNotEncryptedException ex) {
            callback.error(Constants.MSG_FILE_NOT_ENCRYPTED, null);
        }
        catch (Exception ex) {
            callback.error("UNKNOWN " + ex.getMessage(), null);
        }

        throw new IOException("Cannot create FileHandler for " + filename);
    }

    public void start() {
        perf = new Performance(total, progressInterval);
        for (Runner r : runners) {
            r.start();
        }
    }

    public void stop() {
        for (Runner r : runners) {
            r.stop();
        }
    }

    public void pause() {
        for (Runner r : runners) {
            r.pause();
        }
    }

    public void resume() {
        for (Runner r : runners) {
            r.resume();
        }
    }

    public boolean isRunning() {
        for (Runner r : runners) {
            if (r.isRunning()) {
                return true;
            }
        }

        return false;
    }

    private void result(String result) {
        stop();
        callback.result(result);
    }

    private void noresult() {
        boolean isAllStopped = true;
        for (Runner r : runners) {
            if (r.isRunning()) {
                isAllStopped = false;
                break;
            }
        }

        if (isAllStopped) {
            if (cores > 1) {
                perf.addSampleSynchronized(total, true);
            }
            else {
                perf.addSample(total, true);
            }
            callback.noresult();
        }
    }

    private void error(String s, Exception ex) {
        callback.error(s, ex);
    }

    private void progress(String current) {
        if (cores > 1) {
            if (perf.addSampleSynchronized(count.incrementAndGet(), false)) {
                callback.progress(perf, current);
            }
        }
        else {
            if (perf.addSample(count.incrementAndGet(), false)) {
                callback.progress(perf, current);
            }
        }
    }

    private void stopped() {
        ;
    }
}
