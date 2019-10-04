package lang;

import java.io.*;

public class IOContext {
	private String inputFileName;
//	private String outputFileName;
//	private String errFileName;
	private InputStream in;
	private PrintStream out;
	private PrintStream err;

	public InputStream getInStream()	{ return in; }
	public PrintStream getOutStream()	{ return out; }
	public PrintStream getErrStream()	{ return err; }
	public String getInputFileName()	{ return inputFileName; }

	public IOContext(String inputFileName, PrintStream out, PrintStream err) {
		this.out = out;
		this.err = err;
		openInput(inputFileName);
		this.inputFileName = inputFileName;
	}
	private void openInput(String inputFileName) {
		// inputFileNameをオープンしてinにつなぐ
		try {
			in = new FileInputStream(inputFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace(err);
		}
	}
	public void allClose() {
		try {
			if (in != null)  { in.close();  in = null; }
			if (out != null) { out.close(); out = null; }
			if (err != null) { err.close(); err = null; }
		} catch (IOException e) {
			e.printStackTrace(err);
		}
	}
}
