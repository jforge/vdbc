package org.indp.vdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * FIXME actually not a test :)
 * @author pi
 */
public class DatabaseSessionManagerTest {

    private static int cnt = 0;
    private Connection connection;

    public DatabaseSessionManagerTest() {
    }

    @Before
    public void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:db" + (cnt++), "sa", "");
        Statement stmt = connection.createStatement();
        stmt.execute("create table test1(id integer primary key, str varchar(200))");
        stmt.execute("insert into test1 values(1, 'qwert')");
        stmt.execute("insert into test1 values(2, 'asdf')");
        stmt.execute("insert into test1 values(3, 'zxcv')");
        stmt.execute("create table test2(id integer primary key, str1 varchar(200), int1 integer)");
        stmt.execute("insert into test2 values(1, 'qwert', 1)");
        stmt.execute("insert into test2 values(2, 'asdf', 2)");
        stmt.execute("insert into test2 values(3, 'zxcv', 3)");
        stmt.close();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testMetadata() throws SQLException {
        DatabaseMetaData md = connection.getMetaData();
        System.out.println("catalog term = " + md.getCatalogTerm());
        System.out.println("schema term = " + md.getSchemaTerm());
        printCatalogs(md);
        printSchemas(md);
        printTableTypes(md);
        printTables(md);
    }

    protected void printCatalogs(DatabaseMetaData md) throws SQLException {
        ResultSet catalogs = md.getCatalogs();
        System.out.println("Catalogs:");
        while (catalogs.next()) {
            System.out.println(" > " + catalogs.getString(1));
        }
        catalogs.close();
    }

    protected void printSchemas(DatabaseMetaData md) throws SQLException {
        ResultSet schemas = md.getSchemas();
        System.out.println("Schemas:");
        while (schemas.next()) {
            System.out.println(" > " + schemas.getString("TABLE_SCHEM"));
        }
        schemas.close();
    }

    protected void printTableTypes(DatabaseMetaData md) throws SQLException {
        ResultSet schemas = md.getTableTypes();
        System.out.println("Table types:");
        while (schemas.next()) {
            System.out.println(" > " + schemas.getString("TABLE_TYPE"));
        }
        schemas.close();
    }

    protected void printTables(DatabaseMetaData md) throws SQLException {
        ResultSet tables = md.getTables(null, "PUBLIC", null, null);
        System.out.println("Tables:");
        while (tables.next()) {
            System.out.println(" > " + tables.getString("TABLE_NAME") + " / " + tables.getString("TABLE_TYPE"));
        }
        tables.close();
    }
}
