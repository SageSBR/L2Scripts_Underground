//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.LicenseManager;
import ru.akumu.smartguard.utils.IOUtils;
import ru.akumu.smartguard.utils.log.GuardLog;

public class UpdateManager {
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static File updateTemp;
    private static UpdateManager _instance;
    public int MinBuild = -2147483648;
    public int CurrentBuild = 2147483647;
    public long LicenseExpiry = 0L;

    public static UpdateManager getInstance() {
        return _instance != null?_instance:(_instance = new UpdateManager());
    }

    private UpdateManager() {
        File dir = updateTemp.getAbsoluteFile().getParentFile();
        if(!dir.exists()) {
            dir.mkdir();
        }

        if(!executor.isShutdown()) {
            this.updateFromXML();
            executor.execute(new Runnable() {
                public void run() {
                    while(true) {
                        try {
                            UpdateManager.this.downloadUpdates();
                            UpdateManager.this.updateFromXML();
                            Thread.sleep(900000L);
                        } catch (Exception var2) {
                            GuardLog.logException(var2);
                        }
                    }
                }
            });
        }
    }

    public boolean checkBuild(int build) {
        return build >= this.MinBuild && build <= this.CurrentBuild;
    }

    private void updateFromXML() {
        if(updateTemp.exists()) {
            try {
                DocumentBuilderFactory e = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = e.newDocumentBuilder();
                Document doc = docBuilder.parse(updateTemp);
                NodeList nodesBuild = doc.getElementsByTagName("build");
                if(nodesBuild.getLength() > 0) {
                    Element nodesLicense = (Element)nodesBuild.item(0);
                    int license = Integer.parseInt(nodesLicense.getAttribute("minimum").trim());
                    if(license != this.MinBuild) {
                        this.MinBuild = license;
                    }

                    int current = Integer.parseInt(nodesLicense.getAttribute("current").trim());
                    if(current != this.CurrentBuild) {
                        this.CurrentBuild = current;
                    }
                }

                NodeList nodesLicense1 = doc.getElementsByTagName("license");
                if(nodesLicense1.getLength() > 0) {
                    Element license1 = (Element)nodesLicense1.item(0);
                    this.LicenseExpiry = Long.parseLong(license1.getAttribute("expiry").trim());
                }
            } catch (Exception var8) {
                GuardLog.logException(var8);
            }

        }
    }

    private void downloadUpdates() {
        try {
            URL e = new URL(String.format("http://update.smguard.net/build_v2.php?token=%s", new Object[]{LicenseManager.getInstance().LicenseToken}));
            URLConnection conn = e.openConnection();
            conn.setRequestProperty("User-Agent", "SmartGuard/" + LicenseManager.getInstance().LicenseID);
            conn.setDefaultUseCaches(false);
            InputStream in = conn.getInputStream();
            ByteArrayOutputStream boas = null;

            try {
                byte[] e1 = new byte[1024];
                boas = new ByteArrayOutputStream(e1.length);

                int bytesRead;
                while((bytesRead = in.read(e1)) != -1) {
                    boas.write(e1, 0, bytesRead);
                }

                if(boas.size() > 0) {
                    FileOutputStream out = null;

                    try {
                        out = new FileOutputStream(updateTemp);
                        out.write(boas.toByteArray());
                    } catch (Exception var21) {
                        ;
                    } finally {
                        IOUtils.closeQuietly(out);
                    }
                }
            } catch (Exception var23) {
                GuardLog.getLogger().log(Level.WARNING, "Error upon updating SmartGuard versions.");
                GuardLog.logException(var23);
            } finally {
                IOUtils.closeQuietly(boas);
                IOUtils.closeQuietly(in);
            }
        } catch (Exception var25) {
            GuardLog.getLogger().log(Level.WARNING, "Error connecting to build-update server.");
        }

    }

    static {
        updateTemp = new File(GuardConfig.SMART_GUARD_DIR, "./temp/update.xml");
    }
}
