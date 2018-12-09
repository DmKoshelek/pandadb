package by.bsuir.pandadb.core;

import by.bsuir.pandadb.core.dao.DAOInterface;
import by.bsuir.pandadb.core.dao.DAOTables;
import by.bsuir.pandadb.core.facade.SQLFacade;
import by.bsuir.pandadb.core.model.DBTable;
import by.bsuir.pandadb.core.table.impl.ArrayTableImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class PandaDiskWorker {

    private final static String PREFIX_PATH = "/database";
    private final static String REVISION_FILE = PREFIX_PATH + "/revision";
    private final static String PANDA_EXTENSION = ".panda";
    private final static String TABLES_FILE = "/tables" + PANDA_EXTENSION;
    private final static String TABLES_FOLDER = "/tables/";
    private final static String LOG_FILE = "/log" + PANDA_EXTENSION;

    private int currentRevision = 1;

    private final static int READ_WRITE_SIZE = 4 * 1024 * 1024;

    private FileWriter logFileWriter = null;

    @Resource
    private DAOTables daoTables;

    @Resource
    private DAOInterface daoInterface;

    @Resource
    private SQLFacade sqlFacade;

    @PostConstruct
    public void initDB() {
        createDBFolder();
        loadRevision();
        initFactoryTables();
        initTables();
        executeLog();
        currentRevision++;
        removeRevisionDirectory();
        saveStateOfFactoryTables();
        saveTables();
        saveRevision();
        currentRevision--;
        removeRevisionDirectory();
        currentRevision++;
        openLogFile();
    }

    @PreDestroy
    public void destroy() throws IOException {
        if (logFileWriter != null) {
            logFileWriter.close();
        }
    }

    private void createDBFolder() {
        try {
            File file = getFileByLocation(PREFIX_PATH);
            Files.createDirectories(file.toPath());
        } catch (IOException e) {

        }
    }

    private void removeRevisionDirectory() {
        deleteDirectoryStream(getFileByLocation(PREFIX_PATH + getRevisionForPath()).toPath());
    }

    private void loadRevision() {
        try {
            File file = getFileByLocation(REVISION_FILE);
            String newValue = new String(Files.readAllBytes(file.toPath()));
            if (StringUtils.isNotBlank(newValue)) {
                currentRevision = Integer.parseInt(newValue);
            }
        } catch (IOException | NumberFormatException e) {

        }
    }

    private void saveRevision() {
        try {
            File file = getFileByLocation(REVISION_FILE);
            try (FileOutputStream fooStream = new FileOutputStream(file, false)) {
                byte[] value = Integer.toString(currentRevision).getBytes(UTF_8);
                fooStream.write(value);
            }
        } catch (IOException e) {

        }
    }

    private String getRevisionForPath() {
        return "/" + Integer.toString(currentRevision);
    }

    private void initFactoryTables() {
        try {
            File file = getFileByLocation(PREFIX_PATH + getRevisionForPath() + TABLES_FILE);
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                boolean cont = true;

                while (cont) {
                    Object obj = ois.readObject();
                    if (obj != null)
                        daoTables.addTable((DBTable) obj);
                    else
                        cont = false;
                }
            }
        } catch (IOException | ClassNotFoundException | RuntimeException e) {

        }
    }

    private void initTables() {
        List<DBTable> tableList = daoTables.getAllTables();
        byte[] readArray = new byte[READ_WRITE_SIZE];
        for (DBTable table : tableList) {
            try {
                daoInterface.addTable(table.getName(), new ArrayTableImpl());
                File file = getFileByLocation(PREFIX_PATH + getRevisionForPath() + TABLES_FOLDER + table.getName());
                try (FileInputStream fis = new FileInputStream(file)) {
                    int readLen;
                    do {
                        readLen = fis.read(readArray);
                        if (readLen > 0) {
                            byte[] readData = readArray;
                            if (readData.length != readLen) {
                                readData = Arrays.copyOf(readArray, readLen);
                            }
                            daoInterface.addToEnd(table.getName(), readData);
                        }
                    } while (readLen > 0);
                }
            } catch (IOException e) {

            }
        }
    }

    private void saveTables() {
        List<DBTable> tableList = daoTables.getAllTables();
        try {
            Files.createDirectories(getFileByLocation(PREFIX_PATH + getRevisionForPath() + TABLES_FOLDER).toPath());
        } catch (IOException e) {

        }
        for (DBTable table : tableList) {
            long lengthTable = daoInterface.getSize(table.getName());
            try {
                File file = getFileByLocation(PREFIX_PATH + getRevisionForPath() + TABLES_FOLDER + table.getName());
                try (FileOutputStream fis = new FileOutputStream(file, false)) {
                    int offset = 0;
                    long byteLeft = lengthTable;
                    do {
                        byte[] readArray = daoInterface.getByOffset(table.getName(), offset, READ_WRITE_SIZE);
                        int length = (int) Math.min(READ_WRITE_SIZE, byteLeft);
                        fis.write(readArray, 0, length);
                        offset += readArray.length;
                        byteLeft -= readArray.length;
                    } while (byteLeft > 0);
                }
            } catch (IOException e) {

            }
        }
    }

    private void saveStateOfFactoryTables() {
        try {
            File file = getFileByLocation(PREFIX_PATH + getRevisionForPath() + TABLES_FILE);
            Files.createDirectories(file.toPath().getParent());
            Files.createFile(file.toPath());

            try (FileOutputStream fos = new FileOutputStream(file);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {

                daoTables.getAllTables().forEach(o -> {
                    try {
                        oos.writeObject(o);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }

        } catch (IOException | RuntimeException e) {

        }
    }

    private void executeLog() {
        try {
            File file = getFileByLocation(PREFIX_PATH + getRevisionForPath() + LOG_FILE);
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                for (String line; (line = br.readLine()) != null; ) {
                    sqlFacade.executeWithoutLog(line);
                }
            }
        } catch (IOException | RuntimeException e) {

        }
    }

    void deleteDirectoryStream(Path path) {
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {

        }
    }

    private void openLogFile() {
        try {
            File file = getFileByLocation(PREFIX_PATH + getRevisionForPath() + LOG_FILE);
            logFileWriter = new FileWriter(file);
        } catch (IOException e) {

        }
    }

    public void saveToLog(String s) {
        try {
            if (logFileWriter != null) {
                logFileWriter.write(s + "\r\n");
                logFileWriter.flush();
            }
        } catch (IOException e) {

        }
    }

    private File getFileByLocation(String location) {
        String executionPath = System.getProperty("user.dir");
        return new File(executionPath + location);
    }
}
