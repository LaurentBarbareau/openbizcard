import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class VReencodeMCast implements Runnable {

	/**
	 * @param args
	 */
	public static final String DB_USERNAME = "oak";
	public static final String DB_PASSWORD = "123456";
	public static final String DATABASE_NAME = "tmobilemcast_";

	public static final String MOVIE_PATH = "/usr/local/movies/";
	public static final String JEEP_MOVIE_PATH = "/var/www/vhosts/t-mobile.mcasthub.com/httpdocs/web/uploads/";
	private boolean convertFile(int id, String videoPath) {
		videoPath = JEEP_MOVIE_PATH + videoPath;
		int status = 2;
		boolean result = false;

		// ====enter oak's code here
		long currentTime = System.currentTimeMillis();
		String newFileName =currentTime + ".mp4";
		String newFileThumb =currentTime + ".jpg";
//		String newFilePath = getNewFilePath(videoPath, newFileName);
		String newFilePath =MOVIE_PATH + newFileName;
		String newFileThumbnail = getNewFilePath(videoPath, newFileThumb);
		runShellConvert(videoPath, newFilePath,newFileThumbnail);
		// =========================
		result = updateField(id, newFileName , status);
		return result;
	}

	// ==== Oak Add Method for call shell script
	private void runShellConvert(String input, String output, String thumb) {
		try {
			// Runtime.getRuntime().exec("sh convert.sh big.3GP big.mp4");
			// String[] cmds = { "sh", "./convert.sh", "big.3GP", "big.mp4" };
			// Runtime.getRuntime().exec(cmds);

			// String cmd = "ls -l"; // this is the command to execute in the
			// Unix
			String cmd = "sh convert.sh " + input + " " + output + " " + thumb; // 
			// create a process for the shell
			System.out.println("Start Process Builder ");
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			pb.redirectErrorStream(true); // use this to capture messages sent
			// to stderr
			System.out.println("Start Shell ");
			Process shell = pb.start();
			InputStream shellIn = shell.getInputStream(); // this captures the
			// output from the
			// command
			int shellExitStatus = shell.waitFor(); // wait for the shell to
			// finish and get the return
			// code
			// at this point you can process the output issued by the command
			// for instance, this reads the output and writes it to System.out:
			System.out.println("Finish ");
			int c;
			while ((c = shellIn.read()) != -1) {
				System.out.write(c);
			}
			// close the stream
			shellIn.close();
			System.out.println("Stop");
		} catch (Exception e) {
			System.out.println("exception ");
			e.printStackTrace();
		}
	}

	private String getNewFilePath(String fullPath, String newFileName) {
		String[] tests = fullPath.split("/");
		if (tests != null && tests.length > 0) {
			String fileName = tests[tests.length -1];
			return fullPath.replace(fileName, newFileName)  ;
		}
		return null;
	}

	// 
	private boolean updateField(int id,String videoConvertedName, int status) {
		boolean success = false;
		String userName = DB_USERNAME;
		String userPassword = DB_PASSWORD;
		String databaseUrl = "jdbc:mysql://localhost:3306/" + DATABASE_NAME;
		String updateString = "UPDATE  dhl_videos SET  " + "filename_converted  = '" + videoConvertedName + "',"
				+ "status = " + status + " WHERE  dhl_videos.id = '" + id + "';";
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection(databaseUrl,
					userName, userPassword);

			Statement stat = conn.createStatement();
			int result = stat.executeUpdate(updateString);
			if (result != 0) {
				success = true;
				System.out.println("update");
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return success;
	}

	public void searchForUpdate() {
		String userName = DB_USERNAME;
		String userPassword = DB_PASSWORD;
		String databaseUrl = "jdbc:mysql://localhost:3306/" + DATABASE_NAME;
		int updateTime = 5; // unit in sec

		String query = "select id,filename,type from dhl_videos where status=0 and type=0 and TIME_TO_SEC(TIMEDIFF(now(),update_time))>=0 and TIME_TO_SEC(TIMEDIFF(now(),update_time))<="
				+ updateTime;

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection(databaseUrl,
					userName, userPassword);

			Statement stat = conn.createStatement();
			ResultSet result = stat.executeQuery(query);

			while (result.next()) {
				String fileName = result.getString("filename");
				int id = result.getInt("id");
				int type = result.getInt("type");
				if(type == 0 ){
					convertFile(id, fileName);	
				}
				
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected void WaitAMoment() {
		try {
			Thread.sleep(5000);// unit in micro sec
		} catch (InterruptedException e) {
		}
	}

	public void run() {
		while (true) {
			try {
				searchForUpdate();
				// System.out.println("wake");
				WaitAMoment();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
