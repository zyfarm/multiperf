package com.perf.main;

import java.io.FileOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.channels.FileChannel;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;

import com.perf.lib.MainTaskFrame;

public class RunTask {

	public static ByteBuffer buf = ByteBuffer.allocate(1024);

	public static void writeFile(FileOutputStream out, String content)
			throws Exception {
		try {
			Charset chset = Charset.forName("US-ASCII");
			ByteBuffer writebuf = chset.encode(content);
			FileChannel fch = out.getChannel();
			System.out.println(content);
			fch.write(writebuf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testCase(int contype, int type, int concurrency,
			int workernum, Long expiretime, FileOutputStream out) {
		if (type == 0) {
			/**
			 * 使用FixedThreadPoolExecutor,变量是工作者线程数,非并发数
			 */
			for (int i = 4; i <= workernum; i += 2) {
				System.gc();
				MainTaskFrame mainframe = new MainTaskFrame(0, concurrency, i,
						0, expiretime, contype);
				try {
					mainframe.go();
					writeFile(out, i + " " + mainframe.getRT() + " "
							+ mainframe.getUsedMemory() / 1000 + ' ' + '\n');
				} catch (Exception e) {

				}
			}
		} else {
			/**
			 * 使用cachedThreadPoolExecutor, 变量是并发数,非工作者线程数
			 */
			for (int i = 3; i <= concurrency; i += 10) {
				System.gc();
				MainTaskFrame mainframe = new MainTaskFrame(0, i, -1, 1,
						expiretime, contype);
				try {
					mainframe.go();
					writeFile(out, i + " " + mainframe.getRT() + " "
							+ mainframe.getUsedMemory() / 1000 + ' ' + '\n');
				} catch (Exception e) {

				}
			}
		}
	}

	private static void CmdLineProcess(String args[],InnerOption inoption){
		Options options = new Options();
		CommandLine cmd = null;
		try {
			options.addOption("help",false,"Help Info");
			options.addOption("ct", true,"select a concurrent type: 0 truemode; 1 fakemode");
			options.addOption("st", true,"select a ExecutorService: 0 fixed;1 cached");
			options.addOption("cn", true, "Concurrent Number");
			options.addOption("wn", true, "Max worker Number");
			options.addOption("t", true, "Execute Time(milliseconds)");
			options.addOption("file", true, "Output Result File");

			CommandLineParser parser = new GnuParser();
			cmd = parser.parse(options, args);
			if(cmd.hasOption("help")){
				throw new Exception();
			}
			
			if(cmd.hasOption("ct")){
				inoption.setContype(Integer.parseInt(cmd.getOptionValue("ct")));	
			}else{
				inoption.setContype(0);
			}
			

			if(cmd.hasOption("st")){
				inoption.setExecutortype(Integer.parseInt(cmd.getOptionValue("st")));	
			}else{
				inoption.setExecutortype(1);
			}
			
			
			if(cmd.hasOption("cn")){
				inoption.setConnum(Integer.parseInt(cmd.getOptionValue("cn")));	
			}else{
				inoption.setConnum(100);
			}
			
			
			
			if(cmd.hasOption("wn")){
				inoption.setMaxworkernum(Integer.parseInt(cmd.getOptionValue("wn")));	
			}else{
				inoption.setMaxworkernum(Runtime.getRuntime().availableProcessors());
			}
			
		
			if(cmd.hasOption("t")){
				inoption.setExpiretime(Long.parseLong(cmd.getOptionValue("t")));	
			}else{
				inoption.setExpiretime(1000L);
			}
			
			
			if(cmd.hasOption("file")){
				inoption.setFilename(cmd.getOptionValue("file"));
			}else{
				inoption.setFilename("data");
			}
		}catch(Exception e){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("runtask", options);
		}
	}
	
	private static class InnerOption{
		private Integer contype;
		private Integer executortype;
		private Integer connum;
		private Integer maxworkernum;
		private Long expiretime;
		private String filename;
		public Integer getContype() {
			return contype;
		}
		public void setContype(Integer contype) {
			this.contype = contype;
		}
		public Integer getExecutortype() {
			return executortype;
		}
		public void setExecutortype(Integer executortype) {
			this.executortype = executortype;
		}
		public Integer getConnum() {
			return connum;
		}
		public void setConnum(Integer connum) {
			this.connum = connum;
		}
		public Integer getMaxworkernum() {
			return maxworkernum;
		}
		public void setMaxworkernum(Integer maxworkernum) {
			this.maxworkernum = maxworkernum;
		}
		public Long getExpiretime() {
			return expiretime;
		}
		public void setExpiretime(Long expiretime) {
			this.expiretime = expiretime;
		}
		public String getFilename() {
			return filename;
		}
		public void setFilename(String filename) {
			this.filename = filename;
		}
		
		
		
		public InnerOption(Integer contype, Integer executortype,
				Integer connum, Integer maxworkernum, Long expiretime,
				String filename) {
			this.contype = contype;
			this.executortype = executortype;
			this.connum = connum;
			this.maxworkernum = maxworkernum;
			this.expiretime = expiretime;
			this.filename = filename;
		}
		
		
		public InnerOption() {
			this.contype = 0;
			this.executortype = 0;
			this.connum = 0;
			this.maxworkernum = 0;
			this.expiretime = 0L;
			this.filename = "";
		}
	}
	
	
	
	
	public static void main(String[] args) throws Exception {
		/**
		 * concurrenttype,concurrentnum,concurrent,workernum,exectype,expiretime
		 */
		InnerOption inargs=new InnerOption();
		CmdLineProcess(args, inargs);
		if(inargs.getFilename()!=""){
			FileOutputStream out = new FileOutputStream(new File(inargs.getFilename()));
			testCase(inargs.getContype(), inargs.getExecutortype(), inargs.getConnum(), inargs.getMaxworkernum(), inargs.getExpiretime(),out);
			out.close();
		}
		System.exit(0);
	}
}
