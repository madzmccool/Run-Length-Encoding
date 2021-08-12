import java.util.*;
public class RleProgram
{
   public static void main(String [] args)
   {
       /*
       1)Display welcome message
       2)Display color test(ConsoleGfx.testRainbow)
       3)Display the menu
       4)Prompt for input
        */
       boolean exit = false;
       int select;
       String userInput = "";
       Scanner scan = new Scanner(System.in);
       byte[] spectrumImage;
       byte[] currentImage = ConsoleGfx.testRainbow;

       //welcome message
       System.out.println("Welcome to the RLE image encoder!");
       System.out.println("\n  ");
       System.out.println("Displaying Spectrum Image:");
       spectrumImage = ConsoleGfx.testRainbow;
       ConsoleGfx.displayImage(spectrumImage);
       System.out.println("\n");

       do{
           // option menu
           System.out.println("RLE Menu");
           System.out.println("--------");
           System.out.println("0. Exit");
           System.out.println("1. Load File");
           System.out.println("2. Load Test Image");
           System.out.println("3. Read RLE String");
           System.out.println("4. Read RLE Hex String");
           System.out.println("5. Read Data Hex String");
           System.out.println("6. Display Image");
           System.out.println("7. Display RLE String");
           System.out.println("8. Display Hex RLE Data");
           System.out.println("9. Display Hex Flat Data");
           System.out.println("\n");
           System.out.println("Select a Menu Option: ");

           select = scan.nextInt();

           switch (select)
           {
               case 0:
                   System.exit(0);
                   break;
               case 1:
                   System.out.println("Enter name of file to load: ");
                   userInput = scan.next();
                   currentImage = ConsoleGfx.loadFile(userInput);
                   break;
               case 2:
                   currentImage = loadImage(select);
                   break;
               case 3:
                   System.out.println("Enter an RLE string to be decoded: ");
                   userInput = scan.next();
                   currentImage = stringToRle(userInput);
                   break;
               case 4:
                   System.out.println("Enter the hex string holding RLE data: ");
                   userInput = scan.next();
                   currentImage = stringToData(userInput);
                   break;
               case 5:
                   System.out.println("Enter the hex string holding flat data: ");
                   userInput = scan.next();
                   currentImage = stringToData(userInput);
                   break;
               case 6:
                   System.out.println("Displaying image...");
                   ConsoleGfx.displayImage(currentImage);
                   break;
               case 7:
                   String rleString;
                   rleString = toRleString(currentImage);
                   System.out.println("RLE representation: " + rleString);
                   break;
               case 8:
                   String hexString;
                   hexString = toHexString(currentImage);
                   System.out.println("RLE hex values: " + hexString);
                   break;
               case 9:
                   System.out.println("Flat hex values: ");
                   System.out.println(toFlatHexString(currentImage));
                   break;

               default:
                   System.out.println("Error! Invalid input.");
           }

       }while (!exit);
   } //end of main class

   public static String toHexString(byte[] data)
   {        /*
       Translates data (RLE or raw) a hexadecimal string (without delimiters –see examples in standalone section.)
       This method can aid debugging –as students develop, they can display byte arrays to check for errors.
       */
      /* char[] hexChars = new char[data.length * 2];
           for(int i = 0; i < data.length; i++){
               int hex = data[i] & 0x0F;
               hexChars[i * 2] = hexArray[hex >>> 4];
               hexChars[i * 2 + 1] = hexArray[hex & 0x0F];
           }
           return new String(hexChars);
   }//end of toHexString --#1
   */
       int decodedCount = getDecodedLength(encodeRle(data));
       int dataLength = data.length;

       if(decodedCount != dataLength)
           data = encodeRle(data);

       String hexString = "";
       for (int i = 0; i < data.length; i++)
           hexString = hexString + Integer.toHexString((int) data[i]);
       return hexString;
   }

   //private final static char[] hexArray = "0123456789abcdef".toCharArray();

   public static String toFlatHexString(byte[] data)
   {
       String hexString = "";
       for (int i = 0; i < data.length; i++)
           hexString = hexString + Integer.toHexString((int) data[i]);
       return hexString;
   }

   public static byte[] loadImage(int select) //this method is used to load all images
   {
       Scanner scan = new Scanner(System.in);
       byte[] currentImage = ConsoleGfx.testRainbow;
       if(select == 1){
           System.out.println("Enter name of file to load: ");
           String userInput;
           userInput= scan.next();
           currentImage = ConsoleGfx.loadFile(userInput);
           System.out.println("\n");
       }
       else if (select == 2){
           System.out.println("Test image data loaded.");
           System.out.println("\n");
           byte[] testImage;
           testImage = ConsoleGfx.testImage;
           currentImage = testImage;
       }
       return  currentImage;
   }//end of loadImage

   public static int countRuns(byte[] flatData)
   {
       int count = 0;
       int returnCount = 1;
       int currentChar = flatData[0];

       for(int i = 0; i < flatData.length; i++)
       {
           if(currentChar == flatData[i] && count < 15)
           {
               count++;
           }
           else
           {
               returnCount++;
               count = 0;
               currentChar = flatData[i];
           }
       }
       return returnCount;

   }//end of countRuns --#2

   public static byte[] encodeRle(byte [] flatData)
   {
       /*
       Returns an encoding (in RLE) of the raw data passed in.
       This is used to generate the RLE representation of a data set for later viewing or storage.
        */
       ArrayList<Integer> decodedList = new ArrayList<>();
       int currentCount = 0;
       int currentChar = flatData[0];
       for (int i = 0; i < flatData.length; i++) {
           if(flatData[i] == currentChar && currentCount < 15)
           {
               currentCount++;
           }
           else
           {
               decodedList.add(currentCount);
               decodedList.add(currentChar);
               currentCount = 1;
               currentChar = flatData[i];
               if(i == flatData.length - 1)
               {
                   decodedList.add(currentCount);
                   decodedList.add(currentChar);
               }
           }
       }

       byte data[] = new byte[decodedList.size()];
       for(int i = 0; i < decodedList.size(); i++)
           data[i] = decodedList.get(i).byteValue();
       return data;


   }//end of encodeRle -- #3

   public static int getDecodedLength(byte[] rleData)
   {
       /*
       Returns the total size of a set of data in bytes once the given RLE data is decompressed.
       This can be used to decode RLE data into individual data points (such as pixels).
       (Counterpart to #2)
        */

       int returnCount = 0;
       for(int i = 0; i < rleData.length; i=i+2)
       {
           returnCount += rleData[i];
       }
       return returnCount;

   }//end of getDecodedLength -- #4

   public static byte[] decodeRle(byte[] rleData)
   {
       /*
       Returns the decoded data set from RLE encoded data.
       This decompressesRLE data for use.
       (Inverse of #3)
        */

       ArrayList<Integer> decodedList = new ArrayList<>();
       int repeat = 0;
       // do not include height and width position [0],[1]
       for(int i = 0; i < rleData.length; i++)
       {
           if(i%2 == 0)
               repeat = rleData[i];
           else
               for(int j = 0; j < repeat; j++)
                   decodedList.add((int)rleData[i]);

       }

       byte decodedArray[] = new byte[decodedList.size()];
       for(int i = 0; i < decodedList.size(); i++)
           decodedArray[i] = decodedList.get(i).byteValue();

       return decodedArray;

   }//end of decodeRle -- #5

   public static byte[] stringToData(String dataString){
       /*
       Translates a string in hexadecimal format into byte data (can be raw or RLE).
       (Inverse of #1)n
        */
       ArrayList<Integer> stringList = new ArrayList<>();
       for(int i = 0; i < dataString.length(); i++)
           stringList.add(Integer.parseInt(dataString.substring(i,i+1),16));

       byte data[] = new byte[stringList.size()];
       for(int i = 0; i < stringList.size(); i++)
           data[i] = stringList.get(i).byteValue();

       return data;
   } //end of stringToData --#6

   public static String toRleString(byte[] rleData)
   {

        /*
       Translates  RLE  data  into  a  human-readable  representation.
       For each run, in order, it should display the run length in decimal(1-2 digits);
       the run value in hexadecimal(1 digit);
       and a delimiter, ':',
       between runs.
       (See examples in standalone section.)
        */

       int decodedCount = countRuns(decodeRle(rleData));
       int rleLength = rleData.length;

       if(decodedCount * 2 == rleLength)
           rleData = decodeRle(rleData);

       int currentCount = 0;
       int currentChar = rleData[0];
       String rleString = "";

       for (int i = 0; i < rleData.length; i++) {
           if(rleData[i] == currentChar && currentCount < 15)
           {
               currentCount++;
           }
           else
           {
               rleString = rleString + currentCount + Integer.toHexString((currentChar)) + ":";
               currentCount = 1;
               currentChar = rleData[i];
           }
       }

       rleString = rleString + currentCount + Integer.toHexString((currentChar));

       return rleString;
   } //end of toRleString -- #7

   public static byte[] stringToRle(String rleString)
   {
       /*
       Translates a string in human-readable RLE format (with delimiters)
       into RLE byte data.
       (Inverse of #7)
        */
       int repeat;
       ArrayList<String> decodedList = new ArrayList<>();
       String[] delimited = rleString.split(":");
       String value = "";
       for (int i=0; i < delimited.length; i++) {
           if(delimited[i].length() == 3) {
               repeat = Integer.parseInt(delimited[i].substring(0,2));
           }
           else {
               repeat = Integer.parseInt(delimited[i].substring(0,1));
           }

           for(int j = 0; j < repeat; j++)
           {
               value = value + delimited[i].substring(delimited[i].length()-1);
           }

       }

       byte data[] = new byte[value.length()];
       for(int i = 0; i < value.length(); i++)
           data[i] = (byte) Character.getNumericValue(value.charAt(i));
       return data;
   }//end of stringToRle -- #8
   }
