import java.io.IOException;
import java.io.InputStream;

public class Exec {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Start");
		try {
			// Runtime.getRuntime().exec("sh convert.sh big.3GP big.mp4");
//			String[] cmds = { "sh", "./convert.sh", "big.3GP", "big.mp4" };
//			Runtime.getRuntime().exec(cmds);

//			String cmd = "ls -l"; // this is the command to execute in the Unix
			String cmd = "sh convert.sh big.3GP big.mp4"; // 
			// create a process for the shell
			System.out.println("Start Process Builder ");
			ProcessBuilder pb = new ProcessBuilder("bash","-c", cmd);
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

}
