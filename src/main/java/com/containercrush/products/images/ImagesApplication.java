package com.containercrush.products.images;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@SpringBootApplication
@Controller
@CrossOrigin("*")

public class ImagesApplication {
	
	public String endpoint="s3.jp-tok.cloud-object-storage.appdomain.cloud";
	public String bucketName="topplaces";
	public String accessKey="83bdc907b6594ab4b92814d917676591";
	public String secretKey="a15bf0d32e9b7db9425f80aa48a19b7c87fd2017f7c84804";
	public static void main(String[] args) {
		SpringApplication.run(ImagesApplication.class, args);
	}
	
	
	
//	@RequestMapping("getObject")
//	public ModelAndView getObject(@RequestParam("imageName") String imageName)
//	{
//		
//		ModelAndView mv=new ModelAndView();
//				
//		String finalpath=endpoint+"/"+bucketName+"/"+imageName;
//		
//		System.out.println(finalpath);
//		//mv.addObject("path",finalpath);
//		//mv.setViewName("getObject.html");
//		return mv;
//	}

	@RequestMapping(value="listObjects",method=RequestMethod.GET)
	@ResponseBody
	public List<String> listObjects()
	{
		
		List<String> objList=new ArrayList<String>();
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey); // declare a new set of basic credentials that includes the Access Key ID and the Secret Access Key
		AmazonS3 cos = new AmazonS3Client(credentials); // create a constructor for the client by using the declared credentials.
		cos.setEndpoint(endpoint); // set the desired endpoint
		ObjectListing listing = cos.listObjects(bucketName); // get the list of objects in the 'sample' bucket
		List<S3ObjectSummary> summaries = listing.getObjectSummaries(); // create a list of object summaries

		for (S3ObjectSummary obj : summaries){ // for each object...
		  System.out.println("found:"+obj.getKey()); // display 'found: ' and then the name of the object
		  objList.add(obj.getKey());
		}
		return objList;
	}

	@RequestMapping(value="getObjPvt/{objectName}",method=RequestMethod.GET)
	@ResponseBody
	public String getObjPvt(@PathVariable String objectName,HttpServletResponse response)
	{
		
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey); // declare a new set of basic credentials that includes the Access Key ID and the Secret Access Key
		AmazonS3 cos = new AmazonS3Client(credentials); // create a constructor for the client by using the declared credentials.
		cos.setEndpoint(endpoint); // set the desired endpoint
		GetObjectRequest request = new // create a new request to get an object
				GetObjectRequest( // request the new object by identifying
						bucketName, // the name of the bucket
						objectName // the name of the object
				);

				cos.getObject( // write the contents of the object
				request, // using the request that was just created
				new File(objectName) // to write to a new file
				);
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				Calendar c = Calendar.getInstance();
		        c.setTime(date);
		        c.add(Calendar.DATE, 1);
		        Date currentDatePlusOne = c.getTime();
				System.out.println(dateFormat.format(currentDatePlusOne));
				String url=cos.generatePresignedUrl(bucketName, objectName, currentDatePlusOne).toString();
				System.out.print(url);
				response.setContentType("text/plain");
			    response.setCharacterEncoding("UTF-8");
				return url;
	}
}
