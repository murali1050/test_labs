package services;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import javax.xml.bind.DatatypeConverter;

public class Node {

    //Global Variables
    private static final int serverPort = 9090; //Port for this instance of the Server
    private static final String NODE_SERVER = "transfer-dev3-1.cisco.com";
    private static final int NODE_SERVER_PORT = 9092;
    private static final String NODE_SERVER_USER = "node_ftsswcfx";
    private static final String NODE_SERVER_PASS = "ftsswcfx";
    private static String directory = "/";

    public static void main(String[] args) throws IOException {
        //Try to start the Server
        ServerSocket socket = null;
        try
        {
        	trustSelfSignedSSL();
        	System.out.println("**Test 1");
            socket = new ServerSocket(serverPort);
            System.out.println("**Test 2");
            System.out.println("--Node+Connect Java Server--");
            System.out.println("The Server has started on port " + serverPort);
        }
        catch(Exception ex)
        {
            System.out.println("**--Node+Connect Java Server--");
            System.out.println("The Server could not start using port " + serverPort);
            System.out.println("**Error: " + ex);
            System.exit(1);
        }
        //Server has started and we wait for requests
        while(true)
        {
            Socket connection = socket.accept();
            connectionThread conThread = new connectionThread(connection);
            conThread.start();
        }
    }

    static class connectionThread extends Thread
    {
        Socket threadSocket;

        connectionThread(Socket s) {
            threadSocket = s;
        }

        public void run()
        {
            try
            {
                    BufferedReader dataIn = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));
                    BufferedWriter dataOut = new BufferedWriter(new OutputStreamWriter(threadSocket.getOutputStream()));

                    String firstLine = dataIn.readLine();
                    if(firstLine != null)
                    {
                        //This can be changed to get/[url]HTTP based on needs.  This will only service a root page
                        if(firstLine.replace(" ", "").contains("GET/HTTP"))
                        {
                            dataOut.write(getHeader(false));
                            dataOut.write(getPage());
                            dataOut.close();
                            dataIn.close();
                        }

                        if(firstLine.replace(" ", "").contains("POST"))
                        {
                            int dataLength = 0;
                            String s= dataIn.readLine();
                            while(s != null)
                            {
                                if(s.length() == 0)
                                {
                                    break;
                                }
                                if(s.toLowerCase().contains("content-length"))
                                {
                                    String[] split = s.split(" ");
                                    dataLength = Integer.parseInt(split[split.length -1]);
                                }
                                s= dataIn.readLine();
                            }
                            if(dataLength > 0)
                            {
                                char[] dataArray = new char[dataLength];
                                dataIn.read(dataArray);
                                String data = new String(dataArray);
                                data = URLDecoder.decode(data, "UTF-8");

                                //We now have data, we need to see what we got
                                String returnData = "";
                                String[] dataPair = null;
                                String[] firstDataPair = null;
                                String[] secondDataPair = null;
                                //Split Data up
                                if(data.contains("&"))
                                {
                                    //Currently only accept at most two data pairs, this can be changed for more as neede
                                    dataPair = data.split("&");
                                    firstDataPair = dataPair[0].split("=");
                                    secondDataPair = dataPair[1].split("=");
                                }
                                else
                                {
                                    dataPair = data.split("=");
                                }

                                //Returns JSON transferSpec for Connect to start Download
                                if(dataPair[0].contains("download"))
                                {
                                    returnData = getDownloadSpec(dataPair[1]);
                                }

                                //Returns JSON transferSpec for Connect to start Upload
                                else if(dataPair[0].contains("upload"))
                                {
                                    returnData = getUploadSpec(dataPair[1]);
                                }

                                //Returns table data as String (String of HTML content)
                                else if(dataPair[0].contains("changeDirectory"))
                                {
                                    returnData = generateList(dataPair[1]);
                                }

                                //Returns table data as String (String of HTML content)
                                else if(dataPair[0].contains("deleteFile"))
                                {
                                    returnData = deleteFile(dataPair[1]);
                                }

                                //Returns table data as String (String of HTML content)
                                else if(dataPair[0].contains("createDir"))
                                {
                                    returnData = createDir(dataPair[1]);
                                }

                                //Returns table data as String (String of HTML content)
                                else if(firstDataPair[0].contains("renamePath"))
                                {
                                    returnData = renameFile(firstDataPair[1], secondDataPair[1]);
                                }

                                dataOut.write(getHeader(true));
                                dataOut.write(returnData);
                                dataOut.close();
                                dataIn.close();
                            }
                            else
                            {
                                dataOut.close();
                                dataIn.close();
                            }
                        }

                        else
                        {
                            dataOut.write("Sorry.  The server was unable to accept your request.  Please check your URL and try again.");
                            dataOut.close();
                            dataIn.close();
                        }

                    }

            }
            catch(Exception ex){}
        }
    }

    public static String getDownloadSpec(String path)
    {
        String spec = "{ \"transfer_requests\" : [ { \"transfer_request\" : { \"paths\" : [ { \"source\" : \"" + path + "\" } ] } } ] }";
        String download_transfer_spec = "";
        try
        {
            download_transfer_spec = makeNodeRequest("download_setup", spec);
        }
        catch(Exception ex)
        {
            System.out.println("Unable to get Downlaod Spec");
            download_transfer_spec = "{\"error\"";
        }

        return download_transfer_spec;
    }

    public static String getUploadSpec(String path)
    {
        String spec = "{ \"transfer_requests\" : [ { \"transfer_request\" : { \"paths\" : [{}], \"destination_root\" : \"" + path + "\" } } ] }";
        String upload_transfer_spec = "";
        try
        {
            upload_transfer_spec = makeNodeRequest("upload_setup", spec);
        }
        catch(Exception ex)
        {
            System.out.println("Unable to get Upload Spec");
            upload_transfer_spec = "{\"error\"";
        }
        return upload_transfer_spec;
    }

    public static String generateList(String currentDirectory)
    {
        String dir_entries = "";
        String tableContent = "";
        try
        {
            dir_entries = makeNodeRequest("browse", "{ \"path\" : \"" + currentDirectory + "\" }");
        }
        catch(Exception ex)
        {
            return "<tr><td colspan='5'>Failed to Connect to Node.  Please try again. Error: " + ex + "</td></tr>";
        }
        ConcurrentHashMap[] dir_items = getTableItems(dir_entries);
        //No items were returned.  Create single row stating this.
        if (dir_items.length <= 0)
        {
                tableContent = "<tr class='dirListingRow'><td colspan='5'>This directory is empty</td></tr>";
        }
        else
        {
                //Loop through each item returned.  $counter is just for giving HTML items an unique id (you can make it start from anything depending on need), or change foreach to include index
                int counter = 0;
                for(int i=0; i < dir_items.length; i++)
                {
                        tableContent = tableContent +  "<tr class='dirListingRow'>";
                        if(!(dir_items[i].get("type").toString().contains("file")))
                        {
                                tableContent = tableContent +  "<td class='type-col'><span class='glyphicon glyphicon-folder-open' aria-hidden='true'></span></td>";
                                tableContent = tableContent +  "<td class='name-col'><a href='#' onclick='changeDirectory(\"" + dir_items[i].get("path").toString() + "\");return false;'>" + dir_items[i].get("basename").toString() + "</a></td>";
                                tableContent = tableContent +  "<td class='size-col' alt='" + dir_items[i].get("size").toString() + "'>" + cleanSize(Integer.parseInt(dir_items[i].get("size").toString())) + "</td>";
                                tableContent = tableContent +  "<td class='mod-col' alt='" + dir_items[i].get("mtime").toString() + "'>" + cleanDate(dir_items[i].get("mtime").toString()) + "</td>";
                                tableContent = tableContent +  "<td class='act-col'><div class='dropdown'><a href='#' dropdown-toggle' id='dropdownMenu-" + counter + "' data-toggle='dropdown' aria-expanded='true'><span class='glyphicon glyphicon-option-vertical'></span></a>";
                                tableContent = tableContent +  "<ul class='dropdown-menu' role='menu' aria-labelledby='dropdownMenu-" + counter + "'><li role='presentation'><a role='menuitem' tabindex='-1' href='#' onclick='renameFile(\"" + dir_items[i].get("path").toString() + "\");return false;'>Rename</a></li><li role='presentation'><a role='menuitem' tabindex='-1' href='#' onclick='deleteFile(true, \"" + dir_items[i].get("path").toString() + "\");return false;'>Delete</a></li></ul></div></td>";
                        }

                        else
                        {
                                tableContent = tableContent +  "<td class='type-col'><span class='glyphicon glyphicon-file' aria-hidden='true'></span></td>";
                                tableContent = tableContent +  "<td class='name-col'><a class='fileLink' href='#' onclick='downloadFile(this, \"" + dir_items[i].get("path").toString() + "\");return false;'>" + dir_items[i].get("basename").toString() + "</a><span class='progressMessage'></span></td>";
                                tableContent = tableContent +  "<td class='size-col' alt='" + dir_items[i].get("size").toString() + "'>" + cleanSize(Integer.parseInt(dir_items[i].get("size").toString())) + "</td>";
                                tableContent = tableContent +  "<td class='mod-col' alt='" + dir_items[i].get("mtime").toString() + "'>" + cleanDate(dir_items[i].get("mtime").toString()) + "</td>";
                                tableContent = tableContent +  "<td class='act-col'><div class='dropdown'><a href='#' dropdown-toggle' id='dropdownMenu-" + counter + "' data-toggle='dropdown' aria-expanded='true'><span class='glyphicon glyphicon-option-vertical'></span></a>";
                                tableContent = tableContent +  "<ul class='dropdown-menu' role='menu' aria-labelledby='dropdownMenu-" + counter + "'><li role='presentation'><a role='menuitem' tabindex='-1' href='#' onclick='renameFile(\"" + dir_items[i].get("path").toString() + "\");return false;'>Rename</a></li><li role='presentation'><a role='menuitem' tabindex='-1' href='#' onclick='deleteFile(false, \"" + dir_items[i].get("path").toString() + "\");return false;'>Delete</a></li></ul></div></td>";
                        }

                        tableContent = tableContent +  "</tr>";
                        counter++;
                }

        }

        return tableContent;
    }

    public static String deleteFile(String fileName)
    {
        String spec = "{\"paths\" : [ { \"path\" : \"" + fileName + "\" }]}";
        String delete_spec = "";
        try
        {
            delete_spec = makeNodeRequest("delete", spec);
        }
        catch(Exception ex)
        {
            System.out.println("Unable to Send Delete Request");
            delete_spec = "{\"error\"";
        }
        return delete_spec;
    }

    public static String createDir(String fileName)
    {
        String spec = "{\"paths\" : [ { \"path\" : \"" + fileName + "\", \"type\" : \"directory\" }]}";
        String createDir_spec = "";
        try
        {
            createDir_spec = makeNodeRequest("create", spec);
        }
        catch(Exception ex)
        {
            System.out.println("Unable to Send Create Directory Request");
            createDir_spec = "{\"error\"";
        }
        return createDir_spec;
    }

    public static String renameFile(String fullPath, String newName)
    {
        String[] pathParts = fullPath.split("/");
        String fileName = pathParts[pathParts.length-1];
        String path = fullPath.replace(fileName, "");

        String spec = "{\"paths\" : [{ \"path\" : \"" + path + "\", \"source\" : \"" + fileName + "\", \"destination\" : \"" + newName + "\" }]}";
        String rename_spec = "";
        try
        {
            rename_spec = makeNodeRequest("rename", spec);
        }
        catch(Exception ex)
        {
            System.out.println("Unable to Send Rename Request");
            rename_spec = "{\"error\"";
        }
        return rename_spec;
    }

    public static String makeNodeRequest(String command, String spec) throws IOException
    {
    	trustSelfSignedSSL();
    	System.out.println("Command:" + command);
    	System.out.println("Spec:" + spec);
        URL url = new URL("https://" + NODE_SERVER + ":" + NODE_SERVER_PORT + "/files/" + command);
        String authStr = NODE_SERVER_USER + ":" + NODE_SERVER_PASS;
        //String authEncoded = Base64.encode(authStr.getBytes(), false);
        String authEncoded = DatatypeConverter.printBase64Binary(authStr.getBytes());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Basic " + authEncoded);
        connection.setRequestProperty("Content-type", "application/x-javascript");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(spec);
        bw.flush();
        bw.close();

        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        InputStream in = connection.getInputStream();
        OutputStream out = new BufferedOutputStream(outBytes);
        for (int b; (b = in.read()) != -1; ) {
            out.write(b);
        }
        out.close();
        in.close();
        return outBytes.toString();
    }

    //Helper Methods

    public static ConcurrentHashMap[] getTableItems(String json)
    {
        String items = "";
        ConcurrentHashMap[] hashArray;
        try
        {
        Matcher getItems = Pattern.compile("\\[([^\\]]+)").matcher(json);
        if(getItems.find())
        {
            items = getItems.group().replace("[", "").replace("{\n", "").replace("}\n", "");
        }
        String[] itemArray = items.split("},");

        hashArray = new ConcurrentHashMap[itemArray.length];

        for(int i=0; i < itemArray.length; i++)
        {
            ConcurrentHashMap eachItem = new ConcurrentHashMap();
            String[] itemParts = itemArray[i].replace("\n", "").replace(",", ":").replace("\"", "").split(":");
            for(int j=0; j < itemParts.length; j=j+2)
            {
                if(itemParts.length == j+4)
                {
                    eachItem.put(itemParts[j].trim(), itemParts[j+1].trim() + ":" + itemParts[j+2].trim() + ":" + itemParts[j+3].trim());
                    break;
                }
                eachItem.put(itemParts[j].trim(), itemParts[j+1].trim());
            }
            hashArray[i] = eachItem;
        }
        }
        catch(Exception ex)
        {
            hashArray = new ConcurrentHashMap[0];
        }
        return hashArray;

    }

    public static String cleanSize(int size)
    {
        if (size == 0)
	{
            return "";
	}

        String[] units = {"B", "KB", "MB", "GB", "TB"};

    	double base = Math.log(size) / Math.log(1000);
    	return (Math.round(Math.pow(1000, base - Math.floor(base)) * 100.0))/100.0 + " " + units[(int)Math.floor(base)];
    }

    public static String cleanDate(String date)
    {
        String cleanDate = "";
	String[] months = {" ", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] parts = date.split("T");
	String[] dateParts = parts[0].split("-");
	cleanDate = months[Integer.parseInt(dateParts[1])] + " " + dateParts[2] + ", " + dateParts[0] + " ";
	String[] timeParts = parts[1].split("Z")[0].split(":");
	String amPm = "AM";
        int hour = Integer.parseInt(timeParts[0]);
	if(Integer.parseInt(timeParts[0]) > 12)
	{
            amPm = "PM";
            hour = Integer.parseInt(timeParts[0]) - 12;
	}
	cleanDate = cleanDate + hour + ":" + timeParts[1] + ":" + timeParts[2] + " " + amPm;

	return cleanDate;
    }

    //Header and Page Template are below

    public static String getHeader(boolean post)
    {
        String header = "HTTP/1.1 200 OK\r\n";
        if(post)
        {
            header += "cache: no-cache\r\n";
            header += "connection: close\r\n";
            header += "content-type: text; charset=utf-8\r\n";
        }
        else
        {
            header += "content-type:text/html; charset=UTF-8\r\n";
        }

        header += "\r\n";

        return header;
    }

    public static String getPage()
    {
           String pageContent = "<!DOCTYPE html>\n" +
"<html>\n" +
"	<head>\n" +
"		<title>Node + Connect Java Example</title>\n" +
"		<meta charset=\"utf-8\">\n" +
"   	 	<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
"    	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
"    	<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js\"></script>\n" +
"     <script type=\"text/javascript\" src=\"//d3gcli72yxqn2z.cloudfront.net/connect/v4/asperaweb-4.min.js\"></script>\n" +
"	    <script type=\"text/javascript\" src=\"//d3gcli72yxqn2z.cloudfront.net/connect/v4/connectinstaller-4.min.js\"></script>\n" +
"		<link rel=\"stylesheet\" href=\"//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css\">\n" +
"		<script src=\"//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js\"></script>\n" +
"	\n" +
"		<style>\n" +
"			/* Style Goes here, ideally you should put this in a separate CSS file */\n" +
"			*:focus\n" +
"			{\n" +
"				outline:none !Important;\n" +
"			}\n" +
"			body\n" +
"			{\n" +
"				padding: 25px;\n" +
"			}\n" +
"			#uploadArea\n" +
"			{\n" +
"				margin-bottom: 25px;\n" +
"			}\n" +
"			.headerRow, .headerRow a, .headerRow a:hover, .headerRow a:focus, .headerRow a:visited\n" +
"			{\n" +
"				font-weight:bold;\n" +
"				color: #333333;\n" +
"				text-decoration:none;\n" +
"			}\n" +
"			.headerRow\n" +
"			{\n" +
"				cursor: pointer;\n" +
"			}\n" +
"			.type-col\n" +
"			{\n" +
"				width: 7%;\n" +
"				text-align:center;\n" +
"			}\n" +
"			.name-col\n" +
"			{\n" +
"				width: 55%;\n" +
"			}\n" +
"			.size-col\n" +
"			{\n" +
"				width: 10%;\n" +
"			}\n" +
"			.mod-col\n" +
"			{\n" +
"				width: 25%;\n" +
"				white-space:nowrap;\n" +
"			}\n" +
"			.act-col\n" +
"			{\n" +
"				width: 3%;\n" +
"			}\n" +
"			.page-title\n" +
"			{\n" +
"				margin-bottom: 20px;\n" +
"				margin-top:0;\n" +
"			}\n" +
"			#searchBtn\n" +
"			{\n" +
"				min-width:100px;\n" +
"			}\n" +
"			#log-data\n" +
"			{\n" +
"				text-align:right;\n" +
"			}\n" +
"			\n" +
"			#currentDir\n" +
"			{\n" +
"				font-weight:bold;\n" +
"				font-size:18px;\n" +
"			}\n" +
"			#sortIcon\n" +
"			{\n" +
"				float: right;\n" +
"			}\n" +
"			\n" +
"			.sort-icon\n" +
"			{\n" +
"				margin-left: 2px;\n" +
"				display:none;\n" +
"			}\n" +
"			\n" +
"			.sort-icon.sortFirst\n" +
"			{\n" +
"				display:inline !Important;\n" +
"			}\n" +
"			\n" +
"			.sort-icon.sortSecond\n" +
"			{\n" +
"				display:inline !Important;\n" +
"			}\n" +
"			\n" +
"			.sort-icon.sortSecond:before\n" +
"			{\n" +
"				content: '\\e156' !Important;\n" +
"			}\n" +
"			\n" +
"			.progress-td\n" +
"			{\n" +
"				background-color: #DFF2BF;\n" +
"				-webkit-transition: width .6s ease;\n" +
"				-o-transition: width .6s ease;\n" +
"				transition: width .6s ease;\n" +
"			}\n" +
"			.progressMessage {\n" +
"				color: #999;\n" +
"				margin-left: 15px;\n" +
"			}\n" +
"			\n" +
"			#backDiv\n" +
"			{\n" +
"				display:inline-block;\n" +
"				width:0;\n" +
"				margin-right:20px;\n" +
"				overflow:hidden;\n" +
"			}\n" +
"			\n" +
"			#backButton\n" +
"			{\n" +
"				white-space:nowrap;\n" +
"			}\n" +
"			\n" +
"			#pageMenuButton\n" +
"			{\n" +
"				position:relative;\n" +
"				float:right;\n" +
"			}\n" +
"			\n" +
"			#pageMenuButton a\n" +
"			{\n" +
"				color: #333;\n" +
"			}\n" +
"			.searchArea\n" +
"			{\n" +
"				position: relative;\n" +
"			}\n" +
"			#clearSearch\n" +
"			{\n" +
"				position: absolute;\n" +
"				top: 1px;\n" +
"				right: 7px;\n" +
"				font-size: 25px;\n" +
"				color: #ccc;\n" +
"				display:none;\n" +
"			}\n" +
"			\n" +
"			/* Since this version is responsive, we remove the date when on mobile to ensure it looks good on the small screen */\n" +
"			@media screen and (max-width: 700px) {\n" +
"				\n" +
"				.mod-col\n" +
"				{\n" +
"					display:none;\n" +
"				}\n" +
"			}\n" +
"		</style>\n" +
"	\n" +
"		<script>\n" +
"			//JavaScript functions go here.  Ideally this would be in its own file.\n" +
"			\n" +
"			//Functions for handling Node requests\n" +
"			\n" +
"			//Sends POST data to self to get transferSpec\n" +
"			function downloadFile(caller, path)\n" +
"			{\n" +
"				jQuery.ajax({\n" +
"					type: 'POST',\n" +
"					data: { download: path }\n" +
"					})\n" +
"					.done(function(data) {\n" +
"						handleDownload(caller, data)\n" +
"					})\n" +
"					.fail(function(jqXHR, textStatus) {\n" +
"						failure(textStatus);\n" +
"					});\n" +
"			}\n" +
"			\n" +
"			//Sends POST data to self to get transferSpec\n" +
"			function uploadFile()\n" +
"			{\n" +
"				jQuery.ajax({\n" +
"					type: 'POST',\n" +
"					data: { upload: jQuery(\"#currentDirectory\").text() }\n" +
"					})\n" +
"					.done(function(data) {\n" +
"						handleUpload(data)\n" +
"					})\n" +
"					.fail(function(jqXHR, textStatus) {\n" +
"						failure(textStatus);\n" +
"					});\n" +
"			}\n" +
"			\n" +
"			//Sends POST data to self to new table of items\n" +
"			function changeDirectory(path)\n" +
"			{\n" +
"				jQuery.ajax({\n" +
"					type: 'POST',\n" +
"					data: { changeDirectory: path }\n" +
"					})\n" +
"					.done(function(data) {\n" +
"						handleDirectory(data, true, path)\n" +
"					})\n" +
"					.fail(function(jqXHR, textStatus) {\n" +
"						failure(textStatus);\n" +
"					});\n" +
"			}	\n" +
"				\n" +
"			//Sends POST data to self to new table of items\n" +
"			function refreshDirectory()\n" +
"			{\n" +
"				jQuery.ajax({\n" +
"					type: 'POST',\n" +
"					data: { changeDirectory: jQuery(\"#currentDirectory\").text() }\n" +
"					})\n" +
"					.done(function(data) {\n" +
"						handleDirectory(data, false, jQuery(\"#currentDirectory\").text())\n" +
"					})\n" +
"					.fail(function(jqXHR, textStatus) {\n" +
"						failure(textStatus);\n" +
"					});\n" +
"			}	\n" +
"			\n" +
"			//Sends POST data to self to new table of items\n" +
"			function deleteFile(directory, path)\n" +
"			{\n" +
"				//We ask user one more time if they are sure.  This operation cannot be undone so this just gives them a chance to change their mind (this is optional)\n" +
"				if(directory)\n" +
"				{\n" +
"					var message = \"Are you sure you want to delete the directory:\\n\\n'\" + path + \"'?  \\n\\nThis will delete all of the items in this directory.\\nThis operation cannot be undone.\";\n" +
"				}\n" +
"				else\n" +
"				{\n" +
"					var message = \"Are you sure you want to delete the file:\\n\\n'\" + path + \"'?\\n\\nThis operation cannot be undone.\";\n" +
"				}\n" +
"				if (confirm(message)) \n" +
"				{\n" +
"					jQuery.ajax({\n" +
"						type: 'POST',\n" +
"						data: { deleteFile: path }\n" +
"						})\n" +
"						.done(function(data) {\n" +
"							handleDelete(data)\n" +
"						})\n" +
"						.fail(function(jqXHR, textStatus) {\n" +
"							failure(textStatus);\n" +
"					});\n" +
"				} \n" +
"				else {}\n" +
"			}\n" +
"			\n" +
"			//Sends POST data to self to new table of items\n" +
"			function createDirectory()\n" +
"			{\n" +
"				var dirName= jQuery(\"#currentDirectory\").text() + \"/\" + window.prompt(\"Name of Directory?\")\n" +
"				\n" +
"				jQuery.ajax({\n" +
"					type: 'POST',\n" +
"					data: { createDir: dirName }\n" +
"					})\n" +
"					.done(function(data) {\n" +
"						handleDirCreate(data)\n" +
"					})\n" +
"					.fail(function(jqXHR, textStatus) {\n" +
"						failure(textStatus);\n" +
"				});\n" +
"			}\n" +
"			\n" +
"			//Sends POST data to self to new table of items\n" +
"			function renameFile(path)\n" +
"			{\n" +
"				var newName=window.prompt(\"New Name for \" + path + \"?\")\n" +
"				\n" +
"				jQuery.ajax({\n" +
"					type: 'POST',\n" +
"					data: { renamePath: path, renameName:  newName}\n" +
"					})\n" +
"					.done(function(data) {\n" +
"						handleRename(data)\n" +
"					})\n" +
"					.fail(function(jqXHR, textStatus) {\n" +
"						failure(textStatus);\n" +
"				});\n" +
"			}\n" +
"			\n" +
"			//Clicks each file which triggers the onclick event for each file (essentially downloading them all)\n" +
"			function downloadAll()\n" +
"			{\n" +
"				var numFiles = jQuery(\".fileLink\").length;\n" +
"				if (confirm(\"This will download \" + jQuery(\".fileLink\").length + \" files.\\n\\nAre you sure you want to continue?\")) \n" +
"				{\n" +
"					jQuery(\".fileLink\").each(function() {\n" +
"  						jQuery(this).click();\n" +
"					});\n" +
"				}\n" +
"				else{}\n" +
"			}\n" +
"			\n" +
"			//These functions are our handlers.  They take the returned data from the AJAX call and process it\n" +
"			var pathArray;\n" +
"			\n" +
"			//This handles changing the directory for all calls.  'newListing' contains the new table rows returned, 'changed'\n" +
"			//is a boolean stating whether the directory has changed from initial load, 'newPath' is the new directory path.\n" +
"			function handleDirectory(newListing, changed, newPath)\n" +
"			{\n" +
"				//The directory has changed update browsing info on page and show Back Button\n" +
"				if(changed)\n" +
"				{\n" +
"					//Break up new path to each folder for handling\n" +
"					pathArray = newPath.split(\"/\");\n" +
"					pathArray.pop();\n" +
"					var prevDir = pathArray.join(\"/\");\n" +
"					if(prevDir == \"\"){prevDir = jQuery(\"#startingDirectory\").text();}\n" +
"					jQuery(\"#currentDirectory\").text(newPath);\n" +
"					\n" +
"					//Are we at start, if so hide back button\n" +
"					if(jQuery(\"#startingDirectory\").text() == newPath)\n" +
"					{\n" +
"						jQuery(\"#backDiv\").animate({width: \"0px\"});\n" +
"					}\n" +
"					else\n" +
"					{\n" +
"						jQuery(\"#backDiv\").animate({ width: \"100px\"});\n" +
"						jQuery(\"#backButton\").attr(\"onclick\", \"changeDirectory('\" + prevDir + \"');return false;\");\n" +
"					}\n" +
"				}\n" +
"				\n" +
"				jQuery(\".dirListingRow\").remove();\n" +
"				jQuery(\"#fileListTable\").append(newListing);\n" +
"			}\n" +
"			\n" +
"			//Handler for downloads.  'caller' is reference to HTML element who made the call (for updating progress) and 'spec' is the\n" +
"			//transferSpec returned from server.\n" +
"function initConnect(id, callback, caller, spec)\n" +
"{\n" +
"  var asperaWeb = {};\n" +
"  var CONNECT_INSTALLER = '//d3gcli72yxqn2z.cloudfront.net/connect/v4';\n" +
"  var asperaWeb = new AW4.Connect({\n" +
"    sdkLocation: CONNECT_INSTALLER,\n" +
"    minVersion: '3.6.0',\n" +
"    id: \"aspera_web_transfers-\" + id\n" +
"  });\n" +
"  var asperaInstaller = new AW4.ConnectInstaller({\n" +
"    sdkLocation: CONNECT_INSTALLER\n" +
"  });\n" +
"  var statusEventListener = function (eventType, data) {\n" +
"    var status = AW4.Connect.STATUS;\n" +
"    if (eventType === AW4.Connect.EVENT.STATUS) {\n" +
"      if (data === status.INITIALIZING) {\n" +
"        asperaInstaller.showLaunching();\n" +
"      }\n" +
"      if (data === status.FAILED) {\n" +
"        asperaInstaller.showDownload();\n" +
"      }\n" +
"      if (data === status.OUTDATED) {\n" +
"        asperaInstaller.showUpdate();\n" +
"      }\n" +
"      if (data === status.RUNNING) {\n" +
"        asperaInstaller.connected();\n" +
"        callback(caller, spec, asperaWeb, id);\n" +
"      }\n" +
"    }\n" +
"  };\n" +
"  asperaWeb.addEventListener(AW4.Connect.EVENT.STATUS, statusEventListener);\n" +
"  asperaWeb.initSession('nodeConnect-' + id);\n" +
"}\n" +
"\n" +
"//Handler for downloads.  'caller' is reference to HTML element who made the call (for updating progress) and 'spec' is the\n" +
"//transferSpec returned from server.\n" +
"//Starts Connect Client and starts transfer\n" +
"\n" +
"function handleDownload(caller, spec)\n" +
"{\n" +
"  var random = Math.floor((Math.random() * 10000) + 1);\n" +
"  initConnect(random, handleDownloadCallback, caller, spec);\n" +
"}\n" +
"\n" +
"var handleDownloadCallback = function (caller, spec, asperaWeb, random)\n" +
"{\n" +
"    //random is a random number used for creating multiple instances of downloads/uploads\n" +
"    fileControls = {};\n" +
"    var uready = false;\n" +
"\n" +
"    //Progress bar handler\n" +
"    fileControls.handleTransferEvents = function (event, returnObj) {\n" +
"        var obj = returnObj.transfers[0];\n" +
"        if(!obj)\n" +
"        {\n" +
"          obj = {};\n" +
"        }\n" +
"        //Wait for initiating status to avoid reading past events, 'uready' lets us know when we start\n" +
"        if (obj.status == 'initiating' ||  obj.previous_status == 'initiating')\n" +
"        {\n" +
"            uready = true;\n" +
"        }\n" +
"        if (uready)\n" +
"        {\n" +
"			//Received failed, add red background to td and print error message.  Operation is done.\n" +
"            if (obj.status == 'failed')\n" +
"            {\n" +
"                jQuery(caller).closest(\"td\").children(\".progressMessage\").text('The Download Failed: ' + obj.error_desc + ' (' + obj.error_code + ')');\n" +
"                jQuery(caller).closest(\"td\").attr(\"style\", \"background-color: #f2dede;\");\n" +
"                failure(obj.error_desc + ' (' + obj.error_code + ')');\n" +
"                uready = false;\n" +
"            }\n" +
"\n" +
"			//Received completed, add green background to td and print Complete message.  Operation is done.\n" +
"            else if (obj.status == 'completed')\n" +
"            {\n" +
"                jQuery(caller).closest(\"td\").children(\".progressMessage\").text(\"Download Complete\");\n" +
"                jQuery(caller).closest(\"td\").attr(\"style\", \"background-color: #dff0d8;\");\n" +
"                uready = false;\n" +
"                asperaWeb.removeEventListener('transfer');\n" +
"            }\n" +
"			//Transfer in progress, use gradient effect to show background moving (like loading bar).  Need to include gradient for all browsers.\n" +
"            else\n" +
"            {\n" +
"                switch (event) {\n" +
"                    case 'transfer':\n" +
"                        jQuery(caller).closest(\"td\").children(\".progressMessage\").text(\"Downloading (\" + Math.floor(obj.percentage * 100) + \"%)\");\n" +
"                        jQuery(caller).closest(\"td\").attr(\"style\", \"background: -moz-linear-gradient(left, rgba(223,240,216,1) \" + Math.floor(obj.percentage * 100) + \"%, rgba(223,240,216,0) \" + (Math.floor(obj.percentage * 100) + 1) + \"%, rgba(255,255,255,0) \" + (Math.floor(obj.percentage * 100) + 2) + \"%); background: -webkit-gradient(linear, left top, right top, color-stop(\" + Math.floor(obj.percentage * 100) + \"%,rgba(223,240,216,1)), color-stop(\" + (Math.floor(obj.percentage * 100) + 1) + \"%,rgba(223,240,216,0)), color-stop(\" + (Math.floor(obj.percentage * 100) + 2) + \"%,rgba(255,255,255,0))); background: -webkit-linear-gradient(left, rgba(223,240,216,1) \" + Math.floor(obj.percentage * 100) + \"%,rgba(223,240,216,0) \" + (Math.floor(obj.percentage * 100) + 1) + \"%,rgba(255,255,255,0) \" + (Math.floor(obj.percentage * 100) + 2) + \"%); background: -o-linear-gradient(left, rgba(223,240,216,1) \" + Math.floor(obj.percentage * 100) + \"%,rgba(223,240,216,0) \" + (Math.floor(obj.percentage * 100) + 1) + \"%,rgba(255,255,255,0) \" + (Math.floor(obj.percentage * 100) + 2) + \"%); background: -ms-linear-gradient(left, rgba(223,240,216,1) \" + Math.floor(obj.percentage * 100) + \"%,rgba(223,240,216,0) \" + (Math.floor(obj.percentage * 100) + 1) + \"%,rgba(255,255,255,0) \" + (Math.floor(obj.percentage * 100) + 2) + \"%); background: linear-gradient(to right, rgba(223,240,216,1) \" + Math.floor(obj.percentage * 100) + \"%,rgba(223,240,216,0) \" + (Math.floor(obj.percentage * 100) + 1) + \"%,rgba(255,255,255,0) \" + (Math.floor(obj.percentage * 100) + 2) + \"%)\");\n" +
"                        break;\n" +
"                }\n" +
"            }\n" +
"        }\n" +
"    };\n" +
"    asperaWeb.addEventListener('transfer', fileControls.handleTransferEvents);\n" +
"\n" +
"    //Block Connect Dialog from appearing, since we are showing progress on web app\n" +
"    connectSettings = {\n" +
"        \"allow_dialogs\": \"no\"\n" +
"    };\n" +
"\n" +
"    //Start Download using Connect\n" +
"    var transferSpecArray = JSON.parse(spec);\n" +
"    var transferSpec = transferSpecArray.transfer_specs[0].transfer_spec;\n" +
"    //Add token authentication tag to JSON since is it not returned with transferSpec.\n" +
"    transferSpec.authentication = \"token\";\n" +
"    asperaWeb.startTransfer(transferSpec, connectSettings);\n" +
"}\n" +
"\n" +
"//Handler for uploads. 'spec' is the transferSpec returned from server.\n" +
"//Starts Connect Client and starts transfer\n" +
"function handleUpload(spec)\n" +
"{\n" +
"  var random = Math.floor((Math.random() * 10000) + 1);\n" +
"  initConnect(random, handleUploadCallback, undefined, spec);\n" +
"}\n" +
"\n" +
"var handleUploadCallback = function (caller, spec, asperaWeb, random)\n" +
"{\n" +
"	//random is a random number used for creating multiple instances of downloads/uploads\n" +
"    fileControls = {};\n" +
"    var uready = false;\n" +
"\n" +
"	//Progress bar handler\n" +
"    fileControls.handleTransferEvents = function (event, returnObj) {\n" +
"        var obj = returnObj.transfers[0];\n" +
"        if(!obj)\n" +
"        {\n" +
"          obj = {};\n" +
"          jQuery(\"#upload-path\").val(\"The upload has finished or was canceled.\")\n" +
"          uready = false;\n" +
"        }\n" +
"        //Wait for initiating status to avoid reading past events, 'uready' lets us know when we start\n" +
"        if (obj.status == 'initiating' ||  obj.previous_status == 'initiating')\n" +
"        {\n" +
"            uready = true;\n" +
"        }\n" +
"        if (uready)\n" +
"        {\n" +
"			//Received failed, make background red and print error message.  Operation is done.\n" +
"            if (obj.status == 'failed')\n" +
"            {\n" +
"                jQuery(\"#upload-path\").val('The Upload Failed: ' + obj.error_desc + ' (' + obj.error_code + ')');\n" +
"                jQuery(\"#upload-path\").attr(\"style\", \"background-color: #f2dede;\");\n" +
"                failure(obj.error_desc + ' (' + obj.error_code + ')');\n" +
"                uready = false;\n" +
"            }\n" +
"\n" +
"			//Received completed, make background green and print complete message.  Operation is done.\n" +
"            else if (obj.status == 'completed')\n" +
"            {\n" +
"                jQuery(\"#upload-path\").val(\"Uploading Complete\");\n" +
"                jQuery(\"#upload-path\").attr(\"style\", \"background-color: #dff0d8;\");\n" +
"                uready = false;\n" +
"\n" +
"                //Reimplement list of files\n" +
"                changeDirectory(jQuery(\"#currentDirectory\").text());\n" +
"                asperaWeb.removeEventListener('transfer');\n" +
"            }\n" +
"\n" +
"			//Transfer in progress, use gradient effect to show background moving (like loading bar).  Need to include gradient for all browsers.\n" +
"            else\n" +
"            {\n" +
"                switch (event) {\n" +
"                    case 'transfer':\n" +
"                        jQuery(\"#upload-path\").val(\"Uploading (\" + Math.floor(obj.percentage * 100) + \"%)\");\n" +
"                        jQuery(\"#upload-path\").attr(\"style\", \"background: -moz-linear-gradient(left, rgba(223,240,216,1) \" + Math.floor(obj.percentage * 100) + \"%, rgba(223,240,216,0) \" + (Math.floor(obj.percentage * 100) + 1) + \"%, rgba(255,255,255,0) \" + (Math.floor(obj.percentage * 100) + 2) + \"%); background: -webkit-gradient(linear, left top, right top, color-stop(\" + Math.floor(obj.percentage * 100) + \"%,rgba(223,240,216,1)), color-stop(\" + (Math.floor(obj.percentage * 100) + 1) + \"%,rgba(223,240,216,0)), color-stop(\" + (Math.floor(obj.percentage * 100) + 2) + \"%,rgba(255,255,255,0))); background: -webkit-linear-gradient(left, rgba(223,240,216,1) \" + Math.floor(obj.percentage * 100) + \"%,rgba(223,240,216,0) \" + (Math.floor(obj.percentage * 100) + 1) + \"%,rgba(255,255,255,0) \" + (Math.floor(obj.percentage * 100) + 2) + \"%); background: -o-linear-gradient(left, rgba(223,240,216,1) \" + Math.floor(obj.percentage * 100) + \"%,rgba(223,240,216,0) \" + (Math.floor(obj.percentage * 100) + 1) + \"%,rgba(255,255,255,0) \" + (Math.floor(obj.percentage * 100) + 2) + \"%); background: -ms-linear-gradient(left, rgba(223,240,216,1) \" + Math.floor(obj.percentage * 100) + \"%,rgba(223,240,216,0) \" + (Math.floor(obj.percentage * 100) + 1) + \"%,rgba(255,255,255,0) \" + (Math.floor(obj.percentage * 100) + 2) + \"%); background: linear-gradient(to right, rgba(223,240,216,1) \" + Math.floor(obj.percentage * 100) + \"%,rgba(223,240,216,0) \" + (Math.floor(obj.percentage * 100) + 1) + \"%,rgba(255,255,255,0) \" + (Math.floor(obj.percentage * 100) + 2) + \"%)\");\n" +
"                        break;\n" +
"                }\n" +
"            }\n" +
"        }\n" +
"    };\n" +
"\n" +
"    asperaWeb.addEventListener('transfer', fileControls.handleTransferEvents);\n" +
"\n" +
"    //Block Connect Dialog from appearing, since we are showing progress on web app\n" +
"    connectSettings = {\n" +
"        \"allow_dialogs\": \"no\"\n" +
"    };\n" +
"\n" +
"    //Start Upload using Connect\n" +
"    var transferSpecArray = JSON.parse(spec);\n" +
"    var transferSpec = transferSpecArray.transfer_specs[0].transfer_spec;\n" +
"    transferSpec.authentication = \"token\";\n" +
"\n" +
"    //Show Select File dialog box and loop through each file to add it to path array.\n" +
"    asperaWeb.showSelectFileDialog({success: function (pathArray) {\n" +
"            var fileArray = pathArray.dataTransfer.files\n" +
"            for (var i = 0; i < fileArray.length; i++)\n" +
"            {\n" +
"                transferSpec.paths[i] = {source: fileArray[i].name};\n" +
"            }\n" +
"            jQuery(\"#upload-path\").val('Upload Starting');\n" +
"            asperaWeb.startTransfer(transferSpec, connectSettings);\n" +
"        }});\n" +
"}\n" +
"			//Handler for Delete\n" +
"			function handleDelete(returnMessage)\n" +
"			{\n" +
"				//Try and parse an error out of returnMessage, if no error the catch will happen and the directory is refreshed.\n" +
"				try\n" +
"				{\n" +
"					var error = ((returnMessage.split('\"error\":')[1]).split('\"user_message\":')[1]).split('\"')[1];\n" +
"					alert(\"Failed To Delete:\\n\" + error);\n" +
"				}\n" +
"				catch(error)\n" +
"				{	\n" +
"					refreshDirectory();\n" +
"				}\n" +
"			}\n" +
"			\n" +
"			//Handler for creating directory\n" +
"			function handleDirCreate(returnMessage)\n" +
"			{\n" +
"				//Try and parse an error out of returnMessage, if no error the catch will happen and the directory is refreshed.\n" +
"				try\n" +
"				{\n" +
"					var error = ((returnMessage.split('\"error\":')[1]).split('\"user_message\":')[1]).split('\"')[1];\n" +
"					alert(\"Failed To Create Directory:\\n\" + error);\n" +
"				}\n" +
"				catch(error)\n" +
"				{	\n" +
"					refreshDirectory();\n" +
"				}\n" +
"			}\n" +
"			\n" +
"			//Handler for renaming file or directory\n" +
"			function handleRename(returnMessage)\n" +
"			{\n" +
"				//Try and parse an error out of returnMessage, if no error the catch will happen and the directory is refreshed.\n" +
"				try\n" +
"				{\n" +
"					var error = ((returnMessage.split('\"error\":')[1]).split('\"user_message\":')[1]).split('\"')[1];\n" +
"					alert(\"Failed To Rename File:\\n\" + error);\n" +
"				}\n" +
"				catch(error)\n" +
"				{	\n" +
"					refreshDirectory();\n" +
"				}\n" +
"			}\n" +
"			\n" +
"			//Below are functions for the front end\n" +
"			\n" +
"			//Global handling of error messages.  Whenever a function encounters an error message it handles it here\n" +
"			//For this example, the message is printed in the footer.  But you could log it in Console or create a file...		\n" +
"			function failure(message)\n" +
"			{\n" +
"				jQuery(\"#log-data\").text(\"An error occurred: \" + message);\n" +
"			}\n" +
"			\n" +
"			//Comparer for handling sorting of rows.  alt tag is used in date and size for keeping sortable text from console.\n" +
"			function compare(index) {\n" +
"    			return function(a, b) {\n" +
"					if((a.cells.item(index).hasAttribute(\"alt\")) && (b.cells.item(index).hasAttribute(\"alt\")))\n" +
"					{\n" +
"						var valA = a.cells.item(index).getAttribute(\"alt\");\n" +
"						var valB = b.cells.item(index).getAttribute(\"alt\");\n" +
"					}\n" +
"					else\n" +
"					{\n" +
"        				var valA = jQuery(a).children('td').eq(index).html();\n" +
"						var valB = jQuery(b).children('td').eq(index).html()\n" +
"					}\n" +
"        			return jQuery.isNumeric(valA) && jQuery.isNumeric(valB) ? valA - valB : valA.localeCompare(valB);\n" +
"    			}\n" +
"			}\n" +
"			\n" +
"			//Search names of items.  If item does not match search it is hidden.  This triggers after every keystroke\n" +
"			function searchListings()\n" +
"			{\n" +
"				var searchString = jQuery(\"#search-term\").val().toLowerCase();\n" +
"				jQuery(\"td.name-col\").parent(\"tr\").show();\n" +
"				jQuery( \"td.name-col\" ).each(function() {\n" +
"    				if (!((jQuery(this).text().toLowerCase()).indexOf(searchString) > -1))\n" +
"    				{\n" +
"    					jQuery(this).parent(\"tr\").hide();\n" +
"    				}\n" +
"  				});\n" +
"  				if (jQuery(\"#fileListTable tbody\").children(\".dirListingRow:visible\").length == 0)\n" +
"  				{\n" +
"  					//We need to print a message to tell user no results (not just list nothing)\n" +
"  					if(!(jQuery(\"#searchError\").length))\n" +
"  					{\n" +
"  						jQuery(\"#fileListTable\").append(\"<tr id='searchError'><td colspan='5'>Your search returned no results</td></tr>\");\n" +
"  					}\n" +
"  				}\n" +
"  				else\n" +
"  				{\n" +
"  					//We now have results, remove error\n" +
"  					jQuery(\"#searchError\").remove();\n" +
"  				}\n" +
"			}\n" +
"			\n" +
"			//Clear the search box by emptying it and performing search again\n" +
"			function clearSearch()\n" +
"			{\n" +
"				jQuery(\"#search-term\").val(\"\");\n" +
"				searchListings();\n" +
"				jQuery(\"#clearSearch\").fadeOut(\"slow\");\n" +
"			}\n" +
"			\n" +
"			//Functions for when document loads\n" +
"			jQuery(\"document\").ready(function() {\n" +
"				\n" +
"				//Make headerRow clickable for sorting\n" +
"				jQuery('.headerRow').click(function(){\n" +
"	    			var table = jQuery(this).parents('table').eq(0);\n" +
"	    			var rows = table.find('tr:gt(0)').toArray().sort(compare(jQuery(this).index()));\n" +
"	    			this.asc = !this.asc;\n" +
"					\n" +
"					if(!(jQuery(this).children(\".sort-icon\").hasClass(\"sortFirst\")) && !(jQuery(this).children(\".sort-icon\").hasClass(\"sortSecond\")) && (table.hasClass(\"sortedTable\")))\n" +
"					{\n" +
"						jQuery(\".headerRow .sort-icon\").removeClass(\"sortFirst\");\n" +
"						jQuery(\".headerRow .sort-icon\").removeClass(\"sortSecond\");\n" +
"					}\n" +
"					\n" +
"					if(jQuery(this).children(\".sort-icon\").hasClass(\"sortFirst\"))\n" +
"					{\n" +
"						jQuery(this).children(\".sort-icon\").removeClass(\"sortFirst\");\n" +
"						jQuery(this).children(\".sort-icon\").addClass(\"sortSecond\");\n" +
"					}\n" +
"					else if(jQuery(this).children(\".sort-icon\").hasClass(\"sortSecond\"))\n" +
"					{\n" +
"						jQuery(this).children(\".sort-icon\").removeClass(\"sortSecond\");\n" +
"						jQuery(this).children(\".sort-icon\").addClass(\"sortFirst\");\n" +
"					}\n" +
"					else\n" +
"					{\n" +
"						jQuery(this).children(\".sort-icon\").addClass(\"sortFirst\");\n" +
"						table.addClass(\"sortedTable\");\n" +
"					}\n" +
"	    			if (!this.asc)\n" +
"					{\n" +
"						rows = rows.reverse()\n" +
"					}\n" +
"	    			for (var i = 0; i < rows.length; i++)\n" +
"					{\n" +
"						table.append(rows[i])\n" +
"					}\n" +
"				});\n" +
"				\n" +
"				//Trigger search event on keyup (we use keyup since it includes all including backspace)\n" +
"				jQuery('#search-term').bind('keyup', function(e) {\n" +
"					\n" +
"					//Show Clear Button\n" +
"					jQuery(\"#clearSearch\").fadeIn(\"slow\");\n" +
"					searchListings();\n" +
"					if(jQuery(\"#search-term\").val().length == 0)\n" +
"  					{\n" +
"  						jQuery(\"#clearSearch\").fadeOut(\"slow\");\n" +
"  					}\n" +
"				});\n" +
"			});\n" +
"		</script>\n" +
"	</head>\n" +
"\n" +
"\n" +
"	<body>\n" +
"		<h2 class=\"page-title\">(Node + Connect) + Java</h2>\n" +
"		\n" +
"		\n" +
"		<div class=\"row\" id=\"uploadArea\">\n" +
"			<div class=\"col-lg-5\">\n" +
"				<label for=\"upload-path\">Upload File</label>\n" +
"				<div class=\"input-group\">\n" +
"					<input type=\"text\" class=\"form-control\" disabled id=\"upload-path\" placeholder=\"File Path\">\n" +
"				  	<span class=\"input-group-btn\">\n" +
"						<button class=\"btn btn-default\" onclick=\"uploadFile();\" id=\"upload-browse\" type=\"button\">Browse For File</button>\n" +
"				  	</span>\n" +
"				</div>\n" +
"			</div>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"row\">\n" +
"			<div class=\"col-lg-10\">\n" +
"				<div class=\"panel panel-default\">\n" +
"					<div class=\"panel-heading\" id=\"currentDir\"><div id=\"backDiv\"><a id=\"backButton\" href=\"#\" onclick=\"return false;\"><span class=\"glyphicon glyphicon-menu-left\"></span>Back</a></div><div style=\"display:inline-block; overflow:hidden;\">Browsing <span id=\"currentDirectory\">" + directory + "</span></div><div id=\"pageMenuButton\"><a href=\"#\" dropdown-toggle\" id=\"pageMenuDrop\" data-toggle=\"dropdown\" aria-expanded=\"true\"><span class=\"glyphicon glyphicon-cog\"></span></a><ul class=\"dropdown-menu\" role=\"menu\" aria-labelledby=\"pageMenuDrop\"><li role=\"presentation\"><a role=\"menuitem\" tabindex=\"-1\" href=\"#\" onclick=\"createDirectory();return false;\">Create Directory</a></li><li role=\"presentation\"><a role=\"menuitem\" tabindex=\"-1\" href=\"#\" onclick=\"refreshDirectory();return false;\">Refresh</a></li></ul></div></div>\n" +
"						<div class=\"panel-body\">\n" +
"							<div class=\"searchArea\">\n" +
"								<input type=\"text\" class=\"form-control\" id=\"search-term\" placeholder=\"Search\">\n" +
"									<a id=\"clearSearch\" href=\"#\" onclick=\"clearSearch(); return false;\"><span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span></a>\n" +
"							</div>\n" +
"						</div>\n" +
"						<table class=\"table\" id=\"fileListTable\">\n" +
"							<tr>\n" +
"								<th class=\"type-col headerRow\">Type<span class=\"glyphicon glyphicon-sort-by-attributes sort-icon\" aria-hidden=\"true\"></span></th>\n" +
"								<th class=\"name-col headerRow\">Name<span class=\"glyphicon glyphicon-sort-by-attributes sort-icon\" aria-hidden=\"true\"></span></th>\n" +
"								<th class=\"size-col headerRow\">Size<span class=\"glyphicon glyphicon-sort-by-attributes sort-icon\"aria-hidden=\"true\"></span></th>\n" +
"								<th class=\"mod-col headerRow\">Last Modified</th>\n" +
"								<th class=\"act-col\"><span class=\"glyphicon glyphicon-sort-by-attributes\" id=\"sortIcon\" aria-hidden=\"true\"></span></th>\n" +
"							</tr>\n" + generateList(directory) +
"						\n</table>\n" +
"						<div class=\"panel-footer\" id=\"log-data\">Aspera Node</div>\n" +
"					</div>\n" +
"				</div>\n" +
"			</div>\n" +
"		</div>\n" +
"		<div style=\"display:none;\" id=\"startingDirectory\">" + directory + "</div>\n" +
"	</body>\n" +
"</html>";

           return pageContent;
    }
    
    
    /**
	 * This pre-defined method is used to override the 
	 * SSL Exception when communicating to the web services
	 */
	public static void trustSelfSignedSSL() {
	    try {
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        X509TrustManager tm = new X509TrustManager() {

	            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }

				public void checkClientTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
				}
	        };
	        ctx.init(null, new TrustManager[]{tm}, null);
	        SSLContext.setDefault(ctx);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

}

