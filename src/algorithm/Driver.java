package algorithm;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import algorithm.parts.RoundingFunction;

import struct.Input;
import struct.Output;
import xml.Xml;

public class Driver {

	public static void main(String[] args) throws Exception {
		if (args == null || args.length <= 1) {
			args = new String[]{"test.in", "test.out"};
		}
		
		Input in = Xml.input(new File(args[0]));
		Algorithm a = new Greedy();
		Output o = a.run(in);
		PrintWriter pw = new PrintWriter(new FileWriter(new File(args[1])));
		pw.write(o.toXml(""));
		pw.flush();
		pw.close();
	}
	
}
