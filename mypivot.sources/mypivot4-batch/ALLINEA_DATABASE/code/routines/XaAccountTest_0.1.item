package routines;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;




import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atomikos.icatch.jta.UserTransactionManager;

/**
 *
 *
 * A simple program that uses XA-level integration with TransactionsEssentials.
 * It illustrates how you can integrate your own connection pools with Atomikos
 * JTA. Special to this example is that the transaction manager accepts
 * XAResource instances that are unknown to it (meaning that there was no
 * previous setup of some Atomikos-specific resource adapter). <b>NOTE: for this
 * to work it is essential that the jta.properties file contains the setting
 * com.atomikos.icatch.automatic_resource_registration=true </b>
 *
 * Usage: java XaAccount <account number> <operation> [<amount>]<br>
 * where:<br>
 * account number is an integer between 0 and 99<br>
 * and operation is one of (balance, owner, withdraw, deposit).<br>
 * In case of withdraw and deposit, an extra (integer) amount is expected.
 */

public class XaAccountTest {

	private static final int DEFAULT_ACCOUNT_ID = 50;
	// the user name in the database; change if needed.
	// the current settings are empty strings

	private static XADataSource xads = null;
	
	public static void main(String[] args) {
		java.util.Map<String, Object> globalMap = new java.util.HashMap<String, Object>();
		XAConnection conn_tPostgresqlConnection_4 = null;
		globalMap.put("conn_" + "tPostgresqlConnection_4", conn_tPostgresqlConnection_4);
		
		java.sql.Connection conn_tPostgresqlInput_2 = null;
		conn_tPostgresqlInput_2 = (java.sql.Connection) globalMap.get("tPostgresqlConnection_4");
	}

	/**
	 * Setup DB tables if needed.
	 */

	@Before
	public  void checkTables() throws Exception {
		boolean error = false;
		XAConnection xaconn = null;
		try {
			xaconn = getConnection();
		} catch (Exception noConnect) {
			System.err.println("Failed to connect.");
			System.err
					.println("PLEASE MAKE SURE THAT DERBY IS INSTALLED AND RUNNING");
			throw noConnect;
		}

		try {

			Statement s = xaconn.getConnection().createStatement();
			try {
				s.executeQuery("select * from Accounts");
			} catch (SQLException ex) {
				// table not there => create it
				System.err.println("Creating Accounts table...");
				s.executeUpdate("create table Accounts ( "
						+ " account VARCHAR ( 20 ), owner VARCHAR(300), balance DECIMAL (19,0) )");
				for (int i = 0; i < 100; i++) {
					s.executeUpdate("insert into Accounts values ( "
							+ "'account" + i + "' , 'owner" + i + "', 10000 )");
				}
			}
			s.close();
		} catch (Exception e) {
			error = true;
			throw e;
		} finally {
			if (xaconn != null)
				closeConnection(xaconn, error);

		}

		// That concludes setup

	}

	//
	// THE CODE BELOW SHOULD NOT BE CHANGED;
	// IT SHOULD WORK ON ANY JDBC COMPLIANT SYSTEM
	//

	/**
	 * Gets the xa datasource instance.
	 *
	 * @return XADataSource The data source.
	 */

//	@BeforeClass
//	public static  void initXADataSource() throws Exception {
//		// retrieve or construct a third-party XADataSource
//		org.apache.derby.jdbc.EmbeddedXADataSource ds = new org.apache.derby.jdbc.EmbeddedXADataSource();
//		ds.setDatabaseName("db");
//		ds.setCreateDatabase("create");
//		xads = ds;
//	}
	
//	@BeforeClass
//	public static  void initXADataSource() throws Exception {
//		// retrieve or construct a third-party XADataSource
//		org.apache.derby.jdbc.EmbeddedXADataSource ds = new org.apache.derby.jdbc.EmbeddedXADataSource();
//		ds.setDatabaseName("db");
//		ds.setCreateDatabase("create");
//		xads = ds;
//	}



	/**
	 * Utility method to start a transaction and get a connection. This method
	 * also does the enlistment of the XAResource.
	 *
	 * @return XAConnection The xa connection.
	 */

	private  XAConnection getConnection() throws Exception {
		//XADataSource xads = getXADataSource();
		//XAConnection xaconn = null;

		// retrieve (or construct) the TM handle
		TransactionManager tm = new UserTransactionManager();
		tm.setTransactionTimeout(60);
		// First, create a transaction
		tm.begin();

		// xaconn = xads.getXAConnection ( user , passwd );
		XAConnection xaconn = xads.getXAConnection();
		XAResource xares = xaconn.getXAResource();

		// get the current tx
		Transaction tx = tm.getTransaction();
		// enlist; if this is the first time the
		// resource is used then this will also trigger
		// recovery
		tx.enlistResource(xares);

		return xaconn;

	}

	/**
	 * Utility method to close the connection and terminate the transaction.
	 * This method does all XA related tasks and should be called within a
	 * transaction. When it returns, the transaction will be terminated.
	 *
	 * @param xaconn
	 *            The xa connection.
	 * @param error
	 *            Indicates if an error has occurred or not. If true, the
	 *            transaction will be rolled back. If false, the transaction
	 *            will be committed.
	 */

	private  void closeConnection(XAConnection xaconn, boolean error)
			throws Exception {
		if (xaconn != null) {
			int flag = XAResource.TMSUCCESS;
			XAResource xares = xaconn.getXAResource();
			// retrieve or construct a TM handle
			TransactionManager tm = new UserTransactionManager();

			// get the current tx
			Transaction tx = tm.getTransaction();
			// closeConnection
			if (error)
				flag = XAResource.TMFAIL;
			tx.delistResource(xares, flag);
			// close the JDBC user connection
			xaconn.getConnection().close();

			if (error)
				tm.rollback();
			else
				tm.commit();

			// close XAConnection AFTER commit, or commit will fail!
			xaconn.close();
		}
	}

	private  long balance(int account) throws Exception {
		long res = -1;
		boolean error = false;
		XAConnection xaconn = null;

		try {
			xaconn = getConnection();
			Statement s = xaconn.getConnection().createStatement();
			String query = "select balance from Accounts where account='"
					+ "account" + account + "'";
			ResultSet rs = s.executeQuery(query);
			if (rs == null || !rs.next())
				throw new Exception("Account not found: " + account);
			res = rs.getLong(1);
			s.close();
		} catch (Exception e) {
			error = true;
			throw e;
		} finally {
			closeConnection(xaconn, error);
		}
		return res;
	}

	// private static String getOwner ( int account )
	// throws Exception
	// {
	// String res = null;
	// boolean error = false;
	// XAConnection xaconn = null;
	//
	// try {
	// xaconn = getConnection();
	// Statement s = xaconn.getConnection().createStatement();
	// String query = "select owner from Accounts where account='account"
	// + account+"'";
	// ResultSet rs = s.executeQuery ( query );
	// if ( rs == null || !rs.next() )
	// throw new Exception ( "Account not found: " +account );
	// res = rs.getString ( 1 );
	// s.close();
	// }
	// catch ( Exception e ) {
	// error = true;
	// throw e;
	// }
	// finally {
	// closeConnection ( xaconn , error );
	// }
	// return res;
	// }

	private  void withdraw(int account, int amount) throws Exception {
		boolean error = false;
		XAConnection xaconn = null;

		try {
			xaconn = getConnection();
			Statement s = xaconn.getConnection().createStatement();

			String sql = "update Accounts set balance = balance - " + amount
					+ " where account ='account" + account + "'";
			s.executeUpdate(sql);
			s.close();
		} catch (Exception e) {
			error = true;
			throw e;
		} finally {
			closeConnection(xaconn, error);

		}

	}


	@Test
	public void withdraw50OnAccount50() throws Exception {
		long amount1 = balance(DEFAULT_ACCOUNT_ID);

		withdraw(DEFAULT_ACCOUNT_ID, 50);

		long amount2 = balance(DEFAULT_ACCOUNT_ID);

//		Assert.assertEquals(amount1 - 50, amount2);

	}

//	@Test
	public void deposit50OnAccount50() throws Exception {
		long amount1 = balance(DEFAULT_ACCOUNT_ID);

		withdraw(DEFAULT_ACCOUNT_ID, -50);

		long amount2 = balance(DEFAULT_ACCOUNT_ID);

//		Assert.assertEquals(amount1 + 50, amount2);

	}
	// public static void main ( String[] args )
	// {
	// logger.logDebug("PASCALOU");
	// try {
	// //test if DB data has to be created
	// checkTables();
	//
	// if ( args.length < 2 || args.length >3 ) {
	// System.err.println (
	// "Arguments required: <acc. number> <operation> [<amount>]" );
	// System.exit ( 1 );
	// }
	//
	// //get account number
	// int accno = new Integer ( args[0] ).intValue();
	// if ( accno < 0 || accno > 99 ) {
	// System.err.println (
	// "Account number should be between 0 and 99." );
	// System.exit ( 1 );
	// }
	//
	// //get operation
	// String op = args[1];
	//
	// if ( op.equals ( "balance" ) ) {
	// long bal = getBalance ( accno );
	// System.out.println ( "Balance of account " + accno + " is: " + bal );
	// }
	// else if ( op.equals ( "owner" ) ) {
	// String owner = getOwner ( accno );
	// System.out.println ( "Owner of account " + accno + " is: " + owner );
	// }
	// else {
	// //get amount
	// if ( args.length < 3 ) {
	// System.err.println ( "Missing argument: amount." );
	// System.exit ( 1 );
	// }
	// int amount = new Integer ( args[2] ).intValue();
	// if ( op.equals ( "withdraw" ) )
	// withdraw ( accno , amount );
	// else withdraw ( accno , amount * (-1) );
	// }
	//
	//
	// }
	// catch ( Exception e ) {
	// e.printStackTrace();
	// }
	//
	// }

}
