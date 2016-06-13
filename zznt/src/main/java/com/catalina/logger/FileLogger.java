package com.catalina.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import com.catalina.Lifecycle;
import com.catalina.LifecycleException;
import com.catalina.LifecycleListener;
import com.catalina.LifecycleSupport;
import com.util.StringManager;

public class FileLogger extends LoggerBase implements Lifecycle{
	
	private String date = "";
	
	private String directory = "logs";
	
	private String prefix = "catalina.";
	
	private String suffix = ".log";
	
	private StringManager sm =
	        StringManager.getManager(Constants.Package);
	
	protected static final String info =
	        "org.apache.catalina.logger.FileLogger/1.0";
	
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	
	private boolean timestamp = false;
	
	private boolean started = false;
	
	private PrintWriter writer = null;
	
	public String getDirectory() {
        return (directory);
    }

	public void setDirectory(String directory) {

        String oldDirectory = this.directory;
        this.directory = directory;
        support.firePropertyChange("directory", oldDirectory, this.directory);
    }
	
	public String getPrefix() {

        return (prefix);
    }
	
	public void setPrefix(String prefix) {

        String oldPrefix = this.prefix;
        this.prefix = prefix;
        support.firePropertyChange("prefix", oldPrefix, this.prefix);
    }
	
	public String getSuffix() {

        return (suffix);
    }

    public void setSuffix(String suffix) {

        String oldSuffix = this.suffix;
        this.suffix = suffix;
        support.firePropertyChange("suffix", oldSuffix, this.suffix);
    }
	
    public boolean getTimestamp() {

        return (timestamp);
    }
    
    public void setTimestamp(boolean timestamp) {

        boolean oldTimestamp = this.timestamp;
        this.timestamp = timestamp;
        support.firePropertyChange("timestamp", new Boolean(oldTimestamp),
                                   new Boolean(this.timestamp));
    }
    
	@Override
	public void log(String msg) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsString = ts.toString().substring(0, 19);
		String tsDate = tsString.substring(0, 10);
		
		// if the date has changed, switch log files
		if (!date.equals(tsDate)) {
			synchronized (this) {
				if (!date.equals(tsDate)) {
					close();
					date = tsDate;
					open();
				}
			}
		}
		
		// Log this message, timestamped if necessary
		if (writer != null) {
			if (timestamp) {
				writer.println(tsString + " " + msg);
			} else {
				writer.println(msg);
			}
		}
		
	}
	
	private void open() {
		// Create the directory if necessary
        File dir = new File(directory);
        if (!dir.isAbsolute())
            dir = new File(System.getProperty("catalina.base"), directory);
        dir.mkdirs();

        // Open the current log file
        try {
            String pathname = dir.getAbsolutePath() + File.separator +
                prefix + date + suffix;
            writer = new PrintWriter(new FileWriter(pathname, true), true);
        } catch (IOException e) {
            writer = null;
        }
	}

	private void close() {
		if (writer == null)
            return;
        writer.flush();
        writer.close();
        writer = null;
        date = "";
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		lifecycle.addLifecycleListener(listener);
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return lifecycle.findLifecycleListeners();
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		lifecycle.removeLifecycleListener(listener);
	}

	@Override
	public void start() throws LifecycleException {
		// Validate and update our current component state 
		if (started) 
			throw new LifecycleException (sm.getString("fileLogger.alreadyStarted")); 
		lifecycle.fireLifecycleEvent(START_EVENT, null); 
		started = true;
	}

	@Override
	public void stop() throws LifecycleException {
		// Validate and update our current component state 
		if (!started) 
			throw new LifecycleException (sm.getString("fileLogger.notStarted")); 
		lifecycle.fireLifecycleEvent(STOP_EVENT, null); 
		started = false;
		close ();
	}
}
