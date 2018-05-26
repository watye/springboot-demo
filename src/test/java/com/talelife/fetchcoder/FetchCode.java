package com.talelife.fetchcoder;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 根据源代码路径提取编译后的文件
 * <p>
 * 使用说明：
 * 1、在这个类上设置文件输出路径和项目路径
 * 2、提交代码的时候添加任务注释
 * 3、根据任务注释找到svn提交的历史记录
 * 4、在svn历史记录上点右键==》Generate ChangeLog... ==》 svn log with affected paths
 * 5、把文本框里的代码路径复制到 codePath.txt
 * 6、运行本类，生成编译后的代码
 * </p>
 * @author lwy
 *
 */
public class FetchCode {
	
	/**
	 * 文件输出路径
	 */
	private static final String OUTPUT_PATH = "F:/output"; 
	
	/**
	 * 项目路径
	 */
	private static final String PROJECT_PATH = "F:/project/workspace/nets-master/nets-budget";
	
	
	private static final String SOURCE_FILE_NAME = "sourceFile";
	private static final String TARGET_FILE_NAME = "targetFile";
	private static final String TARGET_FOLDER_NAME = "targetFolder";
	private static final List<Map<String,String>> COPYED_FILE = new ArrayList<>();
	
	public static void main(String[] args) throws IOException {
		
		String CODE_LIST_FILE = FetchCode.class.getResource("/com/syswin/util/codeList.txt").getPath().substring(1);
		
		//分析文件
		fetchFileByType(transale(getCodePaths(CODE_LIST_FILE)),PROJECT_PATH,OUTPUT_PATH);
		
		//复制文件
		deleteFolder(new File(OUTPUT_PATH));
		for (Map<String,String> item : COPYED_FILE) {
			mkdirs(item.get(TARGET_FOLDER_NAME));
			try {
				Files.copy(new File(item.get(SOURCE_FILE_NAME)).toPath(), new File(item.get(TARGET_FILE_NAME)).toPath());
			} catch (Exception e) {}
			System.out.println("output file ==> " + item.get(TARGET_FILE_NAME));
		}
		
	}
	
	/**
	 * 删除目录
	 * @param folder
	 */
	private static void deleteFolder(File folder){
		File[] files = folder.listFiles();
		if(Objects.isNull(files)) return;
		
		for (File file : files) {
			if(file.isDirectory()){
				deleteFolder(file);
			}else{
				if(!file.delete()){
					throw new RuntimeException("删除文件失败："+file.getName());
				}
			}
		}
		if(!folder.delete()){
			throw new RuntimeException("删除文件夹失败："+folder.getName());
		}
	}
	
	/**
	 * 转换为目标文件名
	 * @param codePaths
	 * @return
	 */
	private static List<String> transale(List<String> codePaths) {
		List<String> targetCodePaths = new ArrayList<>();
		for (String codePath : codePaths) {
			String ext = getExt(codePath);
			if(".java".equals(ext)){
				String targetPath = codePath.substring(0, codePath.length()-ext.length())+".class";
				targetCodePaths.add(targetPath.replace("src/main/java", "target/classes"));
			}else if(".xml".equals(ext)){
				targetCodePaths.add(codePath.replace("src/main/java", "target/classes"));
			}else{
				targetCodePaths.add(codePath);
			}
		}
		return targetCodePaths;
	}


	private static void fetchFileByType(List<String> codePaths,String fetchBasePath, String outputPath) {

		for (String codePath : codePaths) {
			if(".class".equals(getExt(codePath))){
				fetchClass(codePath,fetchBasePath,outputPath);
			}else if(".xml".equals(getExt(codePath))){
				fetchXml(codePath,fetchBasePath,outputPath);
			}else{
				fetchStatisFile(codePath,fetchBasePath,outputPath);
			}
		}
		
	}

	private static void addOutPutFile(String sourceFile,String targetFile,String targetFolder){
		Map<String,String> item = new HashMap<>();
		item.put(SOURCE_FILE_NAME, sourceFile);
		item.put(TARGET_FILE_NAME, targetFile);
		item.put(TARGET_FOLDER_NAME, targetFolder);
		COPYED_FILE.add(item);
	}
	
	private static void fetchStatisFile(String codePath, String projectPath, String outputPath) {
		String projectName = getProjectName(projectPath);
		String relativePath = "/"+codePath.substring(codePath.indexOf(projectName)+projectName.length()+1);
		String sourceFile = projectPath+relativePath;
		
		String targetFolder = outputPath +"/" +projectName + relativePath.substring(0, relativePath.lastIndexOf('/')).replace("/src/main/webapp/", "/");
		String fileName = relativePath.substring(relativePath.lastIndexOf('/')+1);
		String targetFile = targetFolder + "/" + fileName;
		
		addOutPutFile(sourceFile, targetFile,targetFolder);
		
	}

	private static void fetchXml(String codePath, String projectPath, String outputPath) {
		String projectName = getProjectName(projectPath);
		String relativePath = "/"+codePath.substring(codePath.indexOf(projectName)+projectName.length()+1);
		String sourceFile = projectPath+relativePath;
		
		String targetFolder = outputPath +"/" +projectName + relativePath.substring(0, relativePath.lastIndexOf('/')).replace("/target/", "/WEB-INF/");
		String fileName = relativePath.substring(relativePath.lastIndexOf('/')+1);
		String targetFile = targetFolder + "/" + fileName;
		
		addOutPutFile(sourceFile, targetFile,targetFolder);
		
	}

	/**
	 * 提取class
	 * @param codePath
	 * @param fetchBasePath
	 * @param outputPath
	 */
	private static void fetchClass(String codePath, String projectPath, String outputPath) {
	
		String projectName = getProjectName(projectPath);
		String relativePath = "/"+codePath.substring(codePath.indexOf(projectName)+projectName.length()+1);
		String sourceFile = projectPath+relativePath;
		
		String sourceFolder =  sourceFile.substring(0, sourceFile.lastIndexOf('/'));
		String targetFolder = outputPath +"/" +projectName + relativePath.substring(0, relativePath.lastIndexOf('/')).replace("/target/", "/WEB-INF/");
		String fileName = relativePath.substring(relativePath.lastIndexOf('/')+1);
		String targetFile = targetFolder + "/" + fileName;
		
		addOutPutFile(sourceFile, targetFile,targetFolder);
		
		//复制内部类
		File folder = new File(sourceFolder);
		if(folder.isDirectory()){
			File[] files = folder.listFiles();
			for (File file : files) {
				if(file.getName().startsWith(fileName.substring(0,fileName.lastIndexOf('.'))+"$")){
					addOutPutFile(file.getAbsolutePath(), targetFolder+"/"+file.getName(),targetFolder);
				}
			}
		}
	}

	private static void mkdirs(String targetFolder){
		File folder = new File(targetFolder);
		if(!folder.exists()){
			folder.mkdirs();
		}
	}
	
	private static String getProjectName(String projectPath){
		return projectPath.substring(projectPath.lastIndexOf('/')+1);
	}
	/**
	 * 取扩展名
	 * @param path
	 * @return
	 */
	private static String getExt(String path){
		return path.substring(path.lastIndexOf('.'));
	}
	
	/**
	 * 从文件路径取文件列表
	 * @param path
	 * @return
	 */
	private static List<String> getCodePaths(String path){
		
		try {
			return Files.readAllLines(Paths.get(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
