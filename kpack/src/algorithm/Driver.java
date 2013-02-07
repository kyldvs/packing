package algorithm;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import real.Reak;
import parts.Input;
import xml.Xml;

public class Driver {

	public static void main(String[] args) throws Exception {
		if (args == null || args.length <= 1) {
			System.out.println("Specify files on commandline, input then output.");
			return;
		}
		
		Input in = Xml.input(new File(args[0]));
		String output = new Reak().run(in);
		PrintWriter pw = new PrintWriter(new FileWriter(new File(args[1])));
		pw.write(output);
		pw.flush();
		pw.close();
	}

}
