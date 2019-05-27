package jpa.config.jndi;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.XADataSource;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

public class MainJNDI_JPA_XA {

	public static final String JDNI_NAME_DS = "oracleUCP_DS_XA";

	public static void  printStatistics() throws Exception {
		System.out.println("----------------------------------------------------------");
		System.out.println(pool.getStatistics());
		System.out.println("----------------------------------------------------------");
	}
	
	static {

		System.setProperty("hibernate.generate_statistics", "true");
		System.setProperty("hibernate.format_sql", "true");

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		PatternLayoutEncoder ple = new PatternLayoutEncoder();

		// ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
		// ple.setPattern("%-12date{YYYY-MM-dd HH:mm:ss.SSS} %-5level [%thread]
		// %logger{10} [%file:%line] %msg%n\"");
		// ple.setPattern("%d [%thread] %level %logger - %msg%n");
		ple.setPattern("%-12d{YYYY-MM-dd HH:mm:ss.SSS} [%X{transaction.id}] [%thread] %-5level %logger{36} - %msg%n");
		ple.setContext(lc);
		ple.start();

		ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
		consoleAppender.setEncoder(ple);
		consoleAppender.setContext(lc);
		consoleAppender.start();

		// Context should bet started to set root logger
		Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.addAppender(consoleAppender);
		rootLogger.setLevel(Level.INFO);
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

		Logger statsLogger = (Logger) LoggerFactory
				.getLogger("org.hibernate.engine.internal.StatisticalLoggingSessionEventListener");
		statsLogger.addAppender(consoleAppender);
		statsLogger.setLevel(Level.INFO);
		statsLogger.setAdditive(false); // Avoid multiple logging

		Logger queryTimeLogger = (Logger) LoggerFactory.getLogger("org.hibernate.stat");
		queryTimeLogger.addAppender(consoleAppender);
		queryTimeLogger.setLevel(Level.DEBUG);
		queryTimeLogger.setAdditive(false); // Avoid multiple logging
         
 	}

	protected static EntityManagerFactory emf;
	protected static EntityManager em;

	static void setUp() throws Exception {
		    //Initialize custom JNDI
			System.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY, LocalInitialContextFactory.class.getName());
			InitialContext context = new InitialContext();
			context.bind(JDNI_NAME_DS, getXADatasource());
			 
			emf = Persistence.createEntityManagerFactory("customJNDI_UCP_XA");
			em = emf.createEntityManager();
	}

	public static void main(String[] args) throws Exception  {
       setUp();
         for (int i = 0; i < 10 ; i++) {
           Thread.sleep(1000);
	       Connection con = pool.getXAConnection().getConnection();
	       ResultSet rs =  con.prepareStatement("select CURRENT_TIMESTAMP from dual").executeQuery();
	       while(rs.next()) {
	    	 System.out.println(rs.getString(1));
	       }
	       rs.close();
	       con.close();
	       printStatistics();
         }
         Thread.sleep(600000);
       shutdown();
	}
	
	static String _url = "jdbc:oracle:thin:@(DESCRIPTION=(SOURCE_ROUTE=YES)(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orcl)))";
	static String password = "carclinic";
	static String user = "car_clinic";
	static UniversalConnectionPoolManager ucpm;
    static PoolXADataSource pool;
	//static PoolDataSource pool;
    
	static XADataSource getXADatasource() throws Exception {
		ucpm = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
		ucpm.setLogLevel(java.util.logging.Level.FINEST); //ENABLE UCP LOGGING
		
		pool = PoolDataSourceFactory.getPoolXADataSource(); // XA
		pool.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
		//pool = PoolDataSourceFactory.getPoolDataSource(); //non XA
		//pool.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
		pool.setURL(_url);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setInitialPoolSize(2);
		pool.setMaxPoolSize(5);
		pool.setFastConnectionFailoverEnabled(true);
		pool.setMaxConnectionReuseTime(60); //This causes connection to be closed after 60 seconds and re-created
		return pool;
	}
	
	static void shutdown() throws Exception {
		ucpm.destroyConnectionPool(pool.getConnectionPoolName());
	}
}
