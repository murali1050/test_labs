package services;

public class RegExValidation {

	private static String pattern = "^([a-zA-Z0-9'.@_-]){1,50}$";
//	private static String pattern = "^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$";
	public static void main(String[] args) {
//		String userId = ",12cupgrade1,emily-culp,swccustomer7,jlawrence,Test2011penf,MVDC1_WRN1,rnattama,testuserqaprod1@gmail.com,cabianco,esick,andrew.allison,jki,testdatauser125,tacwebeta8,tacwebeta7,Kraig.Williams,test2011pwrn,tacwebeta6,Test2011CWRN,test011cwrn,qacclt1008,qacclt1005,qacclt1014,qacclt1010,test2011pWRN,TEST2011CWRN,Test2011WRN,EMILY-culp,svorma1,lvfk,Test2011pwrn,tacwebeta9,swccustomer2,test2011PWRN,qacclt1015,12cupgrade1@yopmail.com,eb9trackuser1@yopmail.com,jveerasa,mechawla,imahendr,ordroot-com3,TEST2011PWRN,kwtest002,testingops@yopmail.com,dbakthav,tacwebeta5,test2011cwrn,bdornisch,taishii,carpoulson,psdtest005,CSAT2_WRN,arian.boere,qacclt1013,qacclt1011,qacclt1003,n.aoki,ashdas,IMahendr,ckilday,jprattip,testdatauser121,Test2011cwrn,jkl,tacwebeta4,chilkday,childay,test2011penf,qacclt1012,qacclt1016,swccustomer9,p.srinivasan,weg,kwtest001,ANDREW.allison,vkumarsu,kraig.williams,testdatauser101,test2011CWRN,sophie.deller,sheramal,kmuthaiy,MVDC1_REP,qacclt1004,qacclt1009,sophie.diller,swc_dpl_user_06,ordroot-com4,eb-mdf-int.gen,LVFK,saleff,CSAT2_ENF,MVDC1_ENF,qacclt1006,qacclt1007,qacclt1002,BDORNISCH,testuserqaprod2@gmail.com,swccustomer8,agnanase,tomcthom,sneuser,swcpartner01,csamtestuser2@yopmail.com,swccustomer5,abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		String userId = "d'souza,emily-culp,tsourdon,swccustomer7,rnattama,Test2011penf,testuserqaprod1@gmail.com,jki,andrew.allison,testdatauser125,test011cwrn,tacwebeta8,tacwebeta7,test2011pwrn,Test2011CWRN,shemanoh,tacwebeta6,Test2011WRN,Kraig.Williams,kli,test2011PWRN,jveerasa,tacwebeta9,swccustomer2,sris2,12cupgrade1@yopmail.com,eb9trackuser1@yopmail.com,mechawla,imahendr,TEST2011PWRN,ordroot-com3,testingops@yopmail.com,dbakthav,test2011cwrn,taishii,tacwebeta5,psdtest005,bdornisch,ashdas,jkl,jprattip,Test2011cwrn,testdatauser121,ckilday,vkumarsu,test2011penf,tacwebeta4,swccustomer9,megprabh,weg,kwtest001,test2011CWRN,snuser,sophie.deller,directcust4,testdatauser101,sheramal,sophie.diller,swccustomer8,eb-mdf-int.gen,testuserqaprod2@gmail.com,tomcthom,agnanase,csamtestuser2@yopmail.com";
//		String userId = "d'souza,emily-culp,,khusgupt,khustgupt,swccustomer7,jlawrence,Test2011penf,testuserqaprod1@gmail.com, testuserqaprod2@gmail.com,prdr,srjonnal,Partner1,Tests3swcguest,ordroot-com2,krpydi,cabianco,Anonymous,rmakhija,jafrazie,aparnaraj.rajendran,krwillia,deepeshj,rabalan,adesai2,uramiset,crenus,manojmur,andrew.allison,jki,lvfk,testdatauser125,bindgeor,mugurusa,X:ak1852241,Kraig.Williams,EMILY-culp,marengan,svorma1,sris2,jveerasa,swccustomer2,aarang,ordroot-com5,samoshet,pbaraske,ord-rootcom3,surupate,padiredd,tacwebeta9,mechawla,dkrause,palkkhan,prkishan,eb9trackuser1@yopmail.com,goavinas,arweinbe,12cupgrade1@yopmail.com,ordroot-com3,imahendr,shermal,muralive,mistanfo,arabalas,kwtest002,wilmorga,alpawar,282828507,swcaccess14,swarajas,eb9trackuser2@yopmail.com,eb9trackuser3@yopmail.com,testingops@yopmail.com,testuserqaprod4@gmail.com,dbakthav,carpoulson,sosaini,n.aoki,doconroy,bdornisch,mzellmer,ashdas,IMahendr,khygupta,qaguest.asd@gmail.com,bepedersen8,ckilday,jkl,megprabh,vkumarsu,kjotwani,test2011penf,swc_user.gen,testdatauser121,swccustomer9,shimukhe,mhooping,mkoigura,- testuserqaprod2@gmail.com,kwtest001,tacwebeta4,ANDREW.allison,p.srinivasan,weg,sujitsud,danicoop,kkotrike,sophie.deller,test2011CWRN,sheramal,testdatauser101,ramshar,kmuthaiy,susingh,jchaumont,vijvasud,nturadi,abiala,abcdlll,ssambari,dcn.gen,vkumarsu1,ankarupp,testuserqaprod2@gmail.com,swccustomer8,123343, testuserqaprod1@gmail.com,tomcthom,manjuthom,jsluzewski,- testuserqaprod1@gmail.com,agnanase,adabarjhai,ordroot-com4,fofanah78,swccustomer5,csamtestuser2@yopmail.com,bbhimana";
		String[] users = userId.split(",");
		int success = 0, failure=0;
		for (String user : users) {
			if(validateUserId(user)) {
//				System.out.println("Success:"+user);
				success++;
			} else {
				System.out.println("Failure:"+user);
				failure++;
			}
		}
		System.out.println("Total:" + users.length);
		System.out.println("Success Size:" + success);
		System.out.println("Failure Size:" + failure);

	}

	private static boolean validateUserId(String user) {
		return user.matches(pattern);
	}

}
