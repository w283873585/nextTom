package com.catalina.realm;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.digester.Digester;

import com.catalina.Container;
import com.catalina.LifecycleException;
import com.util.StringManager;

public class MemoryRealm extends RealmBase{
	
	
	// ----------------------------------------------------- Instance Variables


    /**
     * The Container with which this Realm is associated.
     */
    private Container container = null;


    /**
     * The Digester we will use to process in-memory database files.
     */
    private static Digester digester = null;


    /**
     * Descriptive information about this Realm implementation.
     */
    protected final String info =
        "org.apache.catalina.realm.MemoryRealm/1.0";


    /**
     * Descriptive information about this Realm implementation.
     */

    protected static final String name = "MemoryRealm";


    /**
     * The pathname (absolute or relative to Catalina's current working
     * directory) of the XML file containing our database information.
     */
    private String pathname = "conf/tomcat-users.xml";


    /**
     * The set of valid Principals for this Realm, keyed by user name.
     */
    private HashMap principals = new HashMap();


    /**
     * The string manager for this package.
     */
    private static StringManager sm =
        StringManager.getManager(Constants.Package);


    /**
     * Has this component been started?
     */
    private boolean started = false;

    
    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Realm implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

        return info;

    }


    /**
     * Return the pathname of our XML file containing user definitions.
     */
    public String getPathname() {

        return pathname;

    }


    /**
     * Set the pathname of our XML file containing user definitions.  If a
     * relative pathname is specified, it is resolved against "catalina.base".
     *
     * @param pathname The new pathname
     */
    public void setPathname(String pathname) {

        this.pathname = pathname;

    }
	
    // --------------------------------------------------------- Public Methods


    /**
     * Return the Principal associated with the specified username and
     * credentials, if there is one; otherwise return <code>null</code>.
     *
     * @param username Username of the Principal to look up
     * @param credentials Password or other credentials to use in
     *  authenticating this username
     */
    public Principal authenticate(String username, String credentials) {

        GenericPrincipal principal =
            (GenericPrincipal) principals.get(username);

        boolean validated = false;
        if (principal != null) {
            if (hasMessageDigest()) {
                // Hex hashes should be compared case-insensitive
                validated = (digest(credentials)
                             .equalsIgnoreCase(principal.getPassword()));
            } else {
                validated = 
                    (digest(credentials).equals(principal.getPassword()));
            }
        }

        if (validated) {
            if (debug >= 2)
                log(sm.getString("memoryRealm.authenticateSuccess", username));
            return (principal);
        } else {
            if (debug >= 2)
                log(sm.getString("memoryRealm.authenticateFailure", username));
            return (null);
        }

    }
 // -------------------------------------------------------- Package Methods


    /**
     * Add a new user to the in-memory database.
     *
     * @param username User's username
     * @param password User's password (clear text)
     * @param roles Comma-delimited set of roles associated with this user
     */
    void addUser(String username, String password, String roles) {

        // Accumulate the list of roles for this user
        ArrayList list = new ArrayList();
        roles += ",";
        while (true) {
            int comma = roles.indexOf(',');
            if (comma < 0)
                break;
            String role = roles.substring(0, comma).trim();
            list.add(role);
            roles = roles.substring(comma + 1);
        }

        // Construct and cache the Principal for this user
        GenericPrincipal principal =
            new GenericPrincipal(this, username, password, list);
        principals.put(username, principal);

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Return a configured <code>Digester</code> to use for processing
     * the XML input file, creating a new one if necessary.
     */
    protected synchronized Digester getDigester() {

        if (digester == null) {
            digester = new Digester();
            digester.setDebug(this.debug);
            digester.setValidating(false);
            digester.addRuleSet(new MemoryRuleSet());
        }
        return (digester);

    }


    /**
     * Return a short name for this Realm implementation.
     */
    protected String getName() {

        return (this.name);

    }


    /**
     * Return the password associated with the given principal's user name.
     */
    protected String getPassword(String username) {

        GenericPrincipal principal =
            (GenericPrincipal) principals.get(username);
        if (principal != null) {
            return (principal.getPassword());
        } else {
            return (null);
        }

    }


    /**
     * Return the Principal associated with the given user name.
     */
    protected Principal getPrincipal(String username) {

        return (Principal) principals.get(username);

    }


    // ------------------------------------------------------ Lifecycle Methods


    /**
     * Prepare for active use of the public methods of this Component.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents it from being started
     */
    public synchronized void start() throws LifecycleException {

        // Validate the existence of our database file
        File file = new File(pathname);
        if (!file.isAbsolute())
            file = new File(System.getProperty("catalina.base"), pathname);
        if (!file.exists() || !file.canRead())
            throw new LifecycleException
                (sm.getString("memoryRealm.loadExist",
                              file.getAbsolutePath()));

        // Load the contents of the database file
        if (debug >= 1)
            log(sm.getString("memoryRealm.loadPath",
                             file.getAbsolutePath()));
        Digester digester = getDigester();
        try {
            synchronized (digester) {
                digester.push(this);
                digester.parse(file);
            }
        } catch (Exception e) {
            throw new LifecycleException("memoryRealm.readXml", e);
        }

        // Perform normal superclass initialization
        super.start();

    }


    /**
     * Gracefully shut down active use of the public methods of this Component.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that needs to be reported
     */
    public synchronized void stop() throws LifecycleException {

        // Perform normal superclass finalization
        super.stop();

        // No shutdown activities required

    }
	
}
