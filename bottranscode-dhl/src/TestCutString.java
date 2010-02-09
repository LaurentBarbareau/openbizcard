
public class TestCutString {
	public static void main(String [] args){
		String test = "/var/www/vhosts/default/htdocs/demo/storage/big.3gp";
		String [] tests = test.split("/");
		String filename = tests[tests.length -1];
		System.out.println( test.replace(filename, System.currentTimeMillis() + ".mp4") );
	}
}
