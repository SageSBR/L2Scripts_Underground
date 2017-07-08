package l2s.commons.data.xml;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

/**
 * Author: VISTALL
 * Date:  18:35/30.11.2010
 */
public abstract class AbstractDirParser<H extends AbstractHolder> extends AbstractParser<H> {
    protected AbstractDirParser(H holder) {
        super(holder);
    }

    public abstract File getXMLDir();

    public File getCustomXMLDir() {
        return null;
    }

    public abstract boolean isIgnored(File f);

    @Override
    protected final void parse() {
        File dir = getXMLDir();

        if (!dir.exists()) {
            warn("Dir " + dir.getAbsolutePath() + " not exists");
            return;
        }

        parseFiles(dir);

        dir = getCustomXMLDir();

        if (dir == null) {
            return;
        }

        if (!dir.exists()) {
            warn("Dir " + dir.getAbsolutePath() + " not exists");
            return;
        }

        parseFiles(dir);
        afterParseActions();
    }

    private void parseFiles(File dir) {
        try {
            Collection<File> files = FileUtils.listFiles(dir, FileFilterUtils.suffixFileFilter(".xml"), FileFilterUtils.directoryFileFilter());

            files.stream().filter(f -> !f.isHidden()).filter(f -> !isIgnored(f)).forEach(f -> {
                try {
                    parseDocument(new FileInputStream(f), f.getName());
                }
                catch (Exception e) {
                    info("Exception: " + e + " in file: " + f.getName(), e);
                }
            });
        }
        catch (Exception e) {
            warn("Exception: " + e, e);
        }
    }
}