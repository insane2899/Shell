//Programme by Soumik Sen
import java.io.*;
import java.lang.*;
import java.util.*;
import Color.Color;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.History;
import org.jline.reader.impl.history.DefaultHistory;

public class Shell{
	static ProcessBuilder builder;
	static Process process;
	static String pwd;
	static Terminal terminal;
	static LineReader reader;
	static History history;

	static void printDesignation(){
		System.out.print(Color.GREEN_BRIGHT);
		System.out.print("soumik@DeviceNotFound:");
		System.out.print(Color.BLUE_BRIGHT);
		System.out.print(pwd);
		System.out.print(Color.RESET);
	}

	public static void main(String[] args)throws IOException{
		pwd = System.getProperty("user.dir");
		System.out.println("Starting the Terminal:\n\n\n ");
		terminal = TerminalBuilder.terminal();
		history = new DefaultHistory();
		String s;
		while(true){
			printDesignation();
			reader = LineReaderBuilder.builder().terminal(terminal).history(history)
			.completer(new StringsCompleter(getDirectoryStrings())).build();
			System.out.flush();
			s = reader.readLine("$ ");
			if(s.trim().equals("quit")){
				break;
			}
			String[] portions = s.split(";");
			for(int x=0;x<portions.length;x++){
				if(portions[x].contains("|")||portions[x].contains("&")){
					runCommands(portions[x]);
					continue;
				}
				String[] commands = portions[x].trim().split(" ");
				if(commands[0].trim().equals("cd")){
					if(commands.length>2){
						for(int i=2;i<commands.length;i++){
							commands[1]+=" "+commands[i];
						}
					}
					changeDirectory(commands);
					continue;
				}
				runCommands(commands);
			}
		}
	}

	static void changeDirectory(String[] commands){
		if(commands.length>=2){
			String[] sections = commands[1].split("[/]");
			for(int i=0;i<sections.length;i++){
				if(sections[i].trim().equals("..")){
					pwd = removeLastDirectory(pwd);
				}
				else if(sections[i].trim().charAt(0)=='\''){
					if(sections[i].trim().charAt(sections[i].length()-1)=='\''){
						pwd = pwd+"/"+sections[i].trim().substring(1,sections[i].length()-1);
					}
				}
				else{
					pwd = pwd+"/" + sections[i].trim();
				}
			}
		}
		else{
			pwd="/home/soumik";
			return;
		}
	}

	static void runCommands(String[] commands){
		builder = new ProcessBuilder(commands);
		builder.directory(new File(pwd));
		try{
			if(commands[0].trim().equals("vi")){
				commands[0] = "/usr/bin/vi";
			}
			if(commands[0].trim().equals("/usr/bin/vi")){
				builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
				builder.redirectError(ProcessBuilder.Redirect.INHERIT);
				builder.redirectInput(ProcessBuilder.Redirect.INHERIT);
			}
			else{
				builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
				builder.redirectInput(ProcessBuilder.Redirect.PIPE);
				builder.redirectError(ProcessBuilder.Redirect.PIPE);
			}
			process = builder.start();
			if(commands[0].equals("/usr/bin/vi")){
				process.waitFor();
			}
			printOutput(process);
		}catch(Exception e){
			System.out.println(commands[0]+"\n"+e);
		}
		process.destroy();
	}

	static void runCommands(String commands){
		builder = new ProcessBuilder("/bin/sh","-c",commands);
		builder.directory(new File(pwd));
		try{
			Process process = builder.start();
			printOutput(process);
		}catch(Exception e){
			System.out.println(commands+"\n"+e);
		}
		process.destroy();
	}

	static String removeLastDirectory(String s){
		int mark=0;
		for(int i=0;i<s.length();i++){
			if(s.charAt(i)=='/'){
				mark=i;
			}
		}
		return s.substring(0,mark);
	}

	static void printOutput(Process process)throws IOException{
		BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while((line = br2.readLine())!=null){
			System.out.println(line);
		}
		br2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while((line=br2.readLine())!=null){
			System.out.println(line);
		}
	}

	static String[] getDirectoryStrings(){
		ArrayList<String> list = new ArrayList<String>();
		builder = new ProcessBuilder("ls");
		builder.directory(new File(pwd));
		try{
			process = builder.start();
			String line="";
			BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((line=br2.readLine())!=null){
				list.add(line);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		String[] options = new String[list.size()];
		for(int i=0;i<list.size();i++){
			options[i]=list.get(i);
		}
		Arrays.sort(options);
		return options;
	}

}
