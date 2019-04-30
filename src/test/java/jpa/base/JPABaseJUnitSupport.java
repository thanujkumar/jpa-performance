package jpa.base;


import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

public class JPABaseJUnitSupport {

	static {
		
		System.setProperty("hibernate.generate_statistics", "true");
		System.setProperty("hibernate.format_sql", "true");
		
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		PatternLayoutEncoder ple = new PatternLayoutEncoder();

        //ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
		//ple.setPattern("%-12date{YYYY-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{10} [%file:%line] %msg%n\"");
		//ple.setPattern("%d [%thread] %level %logger - %msg%n");
		ple.setPattern("%-12d{YYYY-MM-dd HH:mm:ss.SSS} [%X{transaction.id}] [%thread] %-5level %logger{36} - %msg%n");
        ple.setContext(lc);
        ple.start();
        
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setEncoder(ple);
        consoleAppender.setContext(lc);
        consoleAppender.start();
        
        //Context should bet started to set root logger
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(consoleAppender);
        rootLogger.setLevel(Level.WARN);
        rootLogger.setAdditive(false);
        
        Logger hbm2ddlLogger = (Logger) LoggerFactory.getLogger("org.hibernate.tool.hbm2ddl");
        hbm2ddlLogger.addAppender(consoleAppender);
        hbm2ddlLogger.setLevel(Level.DEBUG);
        hbm2ddlLogger.setAdditive(false);
        
        Logger sqlLogger = (Logger) LoggerFactory.getLogger("org.hibernate.SQL");
        sqlLogger.addAppender(consoleAppender);
        sqlLogger.setLevel(Level.DEBUG);
        sqlLogger.setAdditive(false);
        
        Logger sqlBindLogger = (Logger) LoggerFactory.getLogger("org.hibernate.type.descriptor.sql");
        sqlBindLogger.addAppender(consoleAppender);
        sqlBindLogger.setLevel(Level.TRACE);
        sqlBindLogger.setAdditive(false);
        
        Logger statsLogger = (Logger) LoggerFactory.getLogger("org.hibernate.engine.internal.StatisticalLoggingSessionEventListener");
        statsLogger.addAppender(consoleAppender);
        statsLogger.setLevel(Level.INFO);
        statsLogger.setAdditive(false); //Avoid multiple logging
        
        Logger queryTimeLogger = (Logger) LoggerFactory.getLogger("org.hibernate.stat");
        queryTimeLogger.addAppender(consoleAppender);
        queryTimeLogger.setLevel(Level.DEBUG);
        queryTimeLogger.setAdditive(false); //Avoid multiple logging
 	}
	
	protected static EntityManagerFactory emf;
	protected static EntityManager em;

	@BeforeAll
	static void setUp() {
	  try {	
		emf = Persistence.createEntityManagerFactory("beanValidationJPA");
		em = emf.createEntityManager();
	  } catch (Exception e) {
		  e.printStackTrace();
		  throw e;
	  }
	}
	  

	@AfterAll
	static void cleanUp() {
		em.close();
		emf.close();
	}

	public static void main(String[] args) throws SQLException {
		JPABaseJUnitSupport.setUp();

		em.getTransaction().begin();
		// Get metadata
		Session session = em.unwrap(Session.class);
		session.doWork(connection -> {
			DatabaseMetaData metaData = connection.getMetaData();

			ResultSet resultSet = metaData.getTables(null, null, null, new String[] { "TABLE" });
			System.out.println("Printing TABLE_TYPE \"TABLE\" ");
			System.out.println("----------------------------------");
			while (resultSet.next()) {
				System.out.println(resultSet.getString("TABLE_NAME"));
			}
			System.out.println();

			ResultSet columns = metaData.getColumns(null, null, "BOOK", null);
			while (columns.next()) {
				String columnName = columns.getString("COLUMN_NAME");
				String datatype = columns.getString("DATA_TYPE");
				String columnsize = columns.getString("COLUMN_SIZE");
				String decimaldigits = columns.getString("DECIMAL_DIGITS");
				String isNullable = columns.getString("IS_NULLABLE");
				String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
				// Printing results
				System.out.println(columnName + "---" + datatype + "---" + columnsize + "---" + decimaldigits + "---"
						+ isNullable + "---" + is_autoIncrment);
			}

			// GetPrimarykeys
			ResultSet PK = metaData.getPrimaryKeys(null, null, "BOOK");
			System.out.println("------------PRIMARY KEYS-------------");
			while (PK.next()) {
				System.out.println(PK.getString("COLUMN_NAME") + "===" + PK.getString("PK_NAME"));
			}

		});
		em.getTransaction().commit();
		JPABaseJUnitSupport.cleanUp();
	}
}
