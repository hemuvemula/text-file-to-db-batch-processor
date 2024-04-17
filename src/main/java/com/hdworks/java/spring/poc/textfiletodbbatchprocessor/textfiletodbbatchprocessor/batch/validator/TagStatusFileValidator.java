package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.validator;

public class TagStatusFileValidator {

    public static boolean validateHeader(String header) {
        if (header.length() != 61) {
            return false; // The total length of the header should be 62 characters
        }

        String fileType = header.substring(0, 4);
        String fromAgencyId = header.substring(4, 7);
        String fileDate = header.substring(7, 15);
        String fileTime = header.substring(15, 21);
        String recordCount = header.substring(21, 29);
        String countStat1 = header.substring(29, 37);
        String countStat2 = header.substring(37, 45);
        String countStat3 = header.substring(45, 53);
        String countStat4 = header.substring(53, 61);

        return fileType.equals("ITAG") &&
                fromAgencyId.matches("\\d{3}") &&
                fileDate.matches("\\d{8}") &&
                fileTime.matches("\\d{6}") &&
                recordCount.matches("\\d{8}") &&
                countStat1.matches("\\d{8}") &&
                countStat2.matches("\\d{8}") &&
                countStat3.matches("\\d{8}") &&
                countStat4.matches("\\d{8}");
    }

    public static boolean validateDetail(String detail) {
        if (detail.length() != 18) { // Validate total length of the detail record
            return false;
        }

        String tagAgencyId = detail.substring(0, 3);
        String tagSerialNumber = detail.substring(3, 11);
        String tagStatus = detail.substring(11, 12);
        String tagAcctInfo = detail.substring(12, 18);

        // Validate each field with appropriate conditions
        boolean valid = tagAgencyId.matches("[0-1][0-2][0-7]") &&  // Match values from 000 to 127
                tagSerialNumber.matches("0*([1-9][0-9]{0,6}|1[0-6][0-7][0-7][0-2][0-1][0-5])") &&  // Match values from 00000001 to 16777215
                tagStatus.matches("[123]") &&  // Valid status codes are '1', '2', or '3'
                tagAcctInfo.matches("[0-9A-F]{6}") ;  // Hexadecimal string of 6 characters
        return true;
    }
}

