package correcter;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static FileInputStream inputStream;
    static FileOutputStream outputStream;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
//        System.out.println("Write a mode: ");
//        String mode = scanner.nextLine();
//        String[] read;
//        switch (mode) {
//            case "encode":
//                read = read("send.txt");
//                String[] encode = parity(expand(read));
//                write(encode, "encoded.txt");
//                break;
//            case "send":
//                read = read("encoded.txt");
//                String[] send = send(read);
//                write(send, "received.txt");
//                break;
//            case "decode":
//                read = read("received.txt");
//                String[] decode = decode(read);
//                write(decode, "decoded.txt");
//                break;
//        }
        test();
    }

    public static void test() {
        String[] read;
        read = read("/Users/techorin/Documents/Leisure/Kotlin/Projects/Error Correcting Encoder-Decoder/Error Correcting Encoder-Decoder/task/src/correcter/send.txt");
        for (String s : read) {
            System.out.print(s + " ");
        }
        System.out.println();
        String[] encode = parity(expand(read));
        for (String s : encode) {
            System.out.print(s + " ");
        }
        System.out.println();
        write(encode, "/Users/techorin/Documents/Leisure/Kotlin/Projects/Error Correcting Encoder-Decoder/Error Correcting Encoder-Decoder/task/src/correcter/encoded.txt");
        read = read("/Users/techorin/Documents/Leisure/Kotlin/Projects/Error Correcting Encoder-Decoder/Error Correcting Encoder-Decoder/task/src/correcter/encoded.txt");
        String[] send = send(read);
//        String[] send = encode;
        for (String s : send) {
            System.out.print(s + " ");
        }
        System.out.println();
        write(send, "/Users/techorin/Documents/Leisure/Kotlin/Projects/Error Correcting Encoder-Decoder/Error Correcting Encoder-Decoder/task/src/correcter/received.txt");
        read = read("/Users/techorin/Documents/Leisure/Kotlin/Projects/Error Correcting Encoder-Decoder/Error Correcting Encoder-Decoder/task/src/correcter/received.txt");
        String[] decode = decode(read);
        for (String s : decode) {
            System.out.print(s + " ");
        }
        System.out.println();
        write(decode, "/Users/techorin/Documents/Leisure/Kotlin/Projects/Error Correcting Encoder-Decoder/Error Correcting Encoder-Decoder/task/src/correcter/decoded.txt");
    }

    public static String[] read(String path) {
        byte[] bytes = new byte[0];
        try {
            inputStream = new FileInputStream(path);
            bytes = inputStream.readAllBytes();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] result = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            StringBuilder aux = new StringBuilder(Integer.toBinaryString(bytes[i] & 0xff));
            result[i] = aux.insert(0, "0".repeat(8 - aux.length())).toString();
        }
        return result;
    }

    public static void write(String[] strings, String path) {
        byte[] result = new byte[strings.length];
        for (int i = 0; i < strings.length; i++) {
            result[i] = (byte) Integer.parseInt(strings[i], 2);
        }
        try {
            outputStream = new FileOutputStream(path);
            outputStream.write(result);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] expand(String[] strings) {
        StringBuilder aux = new StringBuilder();
        StringBuilder aux2 = new StringBuilder();
        for (String s : strings) {
            aux.append(s);
        }
        for (int i = 0, c = 0; i < aux.length(); c++) {
            if ("0137".contains("" + c % 8)) {
                aux2.append(".");
            } else {
                aux2.append(aux.charAt(i++));
            }
        }
        return aux2.append(".").toString().split("(?<=\\G.{8})");
    }

    public static String[] parity(String[] strings) {
        String[] result = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            result[i] = strings[i];
            for (int j = 1; j < 8; j *= 2) {
                String offsetBits = "0".repeat(j - 1);
                String parityCheckSubPattern = "1".repeat(j) + "0".repeat(j);
                String parityCheckPattern = (offsetBits + parityCheckSubPattern.repeat((8 - offsetBits.length()) / j)).substring(0, 8);
                parityCheckPattern = parityCheckPattern.replaceFirst("1", "0");
                String parityString = Integer.toBinaryString(Integer.parseInt(result[i].replace(".", "0"), 2) & Integer.parseInt(parityCheckPattern, 2));
                parityString = parityString.substring(0, parityString.length() - 1);
                int parity = (parityString.length() - parityString.replace("1", "").length()) % 2;
                result[i] = result[i].substring(0, j - 1) + parity + result[i].substring(j, 7) + "0";
            }
        }
        return result;
    }

    public static String[] send(String[] strings) {
        String[] result = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            result[i] = strings[i];
            Random r = new Random();
            int index = r.nextInt(7);
            result[i] = result[i].substring(0, index) + (char) ('1' - result[i].charAt(index) + '0') + result[i].substring(index + 1);
        }
        return result;
    }

    public static String[] decode(String[] strings) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            String corrected = strings[i];
            int errorBitPos = 0;
            for (int j = 1; j < 8; j *= 2) {
                String offsetBits = "0".repeat(j - 1);
                String parityCheckSubPattern = "1".repeat(j) + "0".repeat(j);
                String parityCheckPattern = (offsetBits + parityCheckSubPattern.repeat((8 - offsetBits.length()) / j)).substring(0, 8);
                parityCheckPattern = parityCheckPattern.replaceFirst("1", "0");
                String parityString = Integer.toBinaryString(Integer.parseInt(corrected, 2) & Integer.parseInt(parityCheckPattern, 2));
                parityString = parityString.substring(0, parityString.length() - 1);
                int parity = (parityString.length() - parityString.replace("1", "").length()) % 2;
                if (parity + '0' != corrected.charAt(j - 1)) {
                    errorBitPos += j;
                }
            }
            errorBitPos--;
            corrected = corrected.substring(0, errorBitPos) + (char) ('1' - corrected.charAt(errorBitPos) + '0') + corrected.substring(errorBitPos + 1);
            result.append(corrected.charAt(2) + corrected.substring(4, 7));
        }
        return result.toString().split("(?<=\\G.{8})");
    }
}
