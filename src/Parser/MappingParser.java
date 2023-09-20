package Parser;


import Tokenizer.TokenizerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MappingParser implements Parser {

    private enum instruction {
        add, nand, lw, sw, beq, jalr, halt, noop
    }

    private boolean findEnum(String s) {
        for (instruction i : instruction.values()) {
            if (i.name().equals(s)) {
                return true;
            }
        }
        return false;
    }

    private final ArrayList<ArrayList<String>> mappingInstruction;
    private ArrayList<String> binaryCode;
    private final HashMap<String, ArrayList<Integer>> labels;
    private final HashMap<String, String> opcodeTable = new HashMap<>();
    private final String Bit31_25 = "0000000";

    public MappingParser(ArrayList<ArrayList<String>> mappingInstruction) {
        this.mappingInstruction = mappingInstruction;
        binaryCode = new ArrayList<>();
        labels = new HashMap<>();
        opcodeTable.put("add", "000");
        opcodeTable.put("nand", "001");
        opcodeTable.put("lw", "010");
        opcodeTable.put("sw", "011");
        opcodeTable.put("beq", "100");
        opcodeTable.put("jalr", "101");
        opcodeTable.put("halt", "110");
        opcodeTable.put("noop", "111");
    }

    @Override
    public String PrintBinaryFile() {
        MappingInstruction();
        return null;
    }

    private void MappingInstruction() {
        CreateLabelsTable();
        int line = 0;
        for (ArrayList<String> instructions : mappingInstruction) {
            line++;
            String binary = "";
            if (!instructions.isEmpty()) { // to check is in not case last member is empty list
                if (findEnum(instructions.get(0))) {
                    String instruction = instructions.get(0);
                    String Bit24_22 = opcodeTable.get(instructions.get(0));
                    binary = "";
                    try{
                        switch (instruction) {
                            case "add", "nand" -> {//R-type
                                String Bit21_19 = ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(1))));
                                String Bit18_16 = ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(2))));
                                String Bit15_3 = "0000000000000";
                                String Bit2_0 = ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(3))));
                                binary = Bit31_25 + Bit24_22 + Bit21_19 + Bit18_16 + Bit15_3 + Bit2_0;
                            }
                            case "lw", "sw", "beq" -> {//I-type
                                String Bit21_19 = ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(1))));
                                String Bit18_16 = ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(2))));
                                String Bit15_0;
                                if (isNmu(instructions.get(3))) {
                                    if (instruction.equals("beq")) {
                                        int offset = Integer.parseInt(instructions.get(3));
                                        String off = ExtendTo16Bit(Integer.toBinaryString(offset));
                                        Bit15_0 = ExtendTo16Bit(off);
                                    } else {
                                        Bit15_0 = ExtendTo16Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(3))));
                                    }
                                } else {
                                    if (labels.get(instructions.get(3)) == null) {
                                        System.exit(1);
                                    }
                                    if (instruction.equals("beq")) {
                                        int offset = labels.get(instructions.get(3)).get(0) - line;
                                        String off = ExtendTo16Bit(Integer.toBinaryString(offset));
                                        Bit15_0 = ExtendTo16Bit(off);
                                    } else {
                                        int offset = labels.get(instructions.get(3)).get(0);
                                        Bit15_0 = ExtendTo16Bit(Integer.toBinaryString(offset));
                                    }
                                }
                                binary = Bit31_25 + Bit24_22 + Bit21_19 + Bit18_16 + Bit15_0;
                            }
                            case "jalr" -> {//J-typr
                                String Bit21_19 = ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(1))));
                                String Bit18_16 = ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(2))));
                                String Bit15_0 = "0000000000000000";
                                binary = Bit31_25 + Bit24_22 + Bit21_19 + Bit18_16 + Bit15_0;
                            }
                            case "halt", "noop" -> binary = Bit31_25 + Bit24_22 + "0000000000000000000000";//O-type
                        }
                    }catch (Exception e) {
                        System.exit(1);
                    }
                } else {
                    if(isNmu(instructions.get(1))){
                        binary = ExtendTo32Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(1))));
                    }else{
                        binary = ExtendTo32Bit(Integer.toBinaryString(labels.get(instructions.get(1)).get(0)));
                    }
                }
                binaryCode.add(binary);
            }
        }
    }

    private String ExtendTo3Bit(String number) {
        StringBuilder numberBuilder = new StringBuilder(number);
        for (int i = numberBuilder.length(); i < 3; i++) {
            numberBuilder.insert(0, "0");
        }
        return numberBuilder.toString();
    }

    private String ExtendTo16Bit(String number){
        if(number.length() == 32){
            number = number.substring(16);
        }else {
            StringBuilder numberBuilder = new StringBuilder(number);
            for (int i = numberBuilder.length(); i < 16; i++) {
                numberBuilder.insert(0, "0");
            }
            number = numberBuilder.toString();
        }
        return number;
    }

    private String ExtendTo32Bit(String number){
        if(number.length() != 32){
            StringBuilder numberBuilder = new StringBuilder(number);
            for (int i = numberBuilder.length(); i < 32; i++) {
                numberBuilder.insert(0, "0");
            }
            number = numberBuilder.toString();
        }
        return number;
    }

    private boolean isNmu(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void CreateLabelsTable() {
        for (int i = 0; i < mappingInstruction.size() - 1; i++) {
            if (!findEnum(mappingInstruction.get(i).get(0))) {//if to check is label?
                String word = mappingInstruction.get(i).get(0);
                if (Character.isDigit(word.charAt(0)) || word.equals(".fill")) {// if check label start with number or .fill
                    throw new TokenizerException.BadCharacter(word.charAt(0));
                }

                ArrayList<Integer> values = new ArrayList<>();
                values.add(i); // add # line

                if (mappingInstruction.get(i).get(1).equals(".fill")) {//in this case to check is .fill case
                    try {
                        values.add(Integer.parseInt(mappingInstruction.get(i).get(2)));
                    } catch (Exception e) {
                        if (labels.get(mappingInstruction.get(i).get(2)) == null) {
                            throw new TokenizerException.BadCharacter('0');
                        } else {
                            int line = labels.get(mappingInstruction.get(i).get(2)).get(0);
                            values.add(line);
                        }
                    }
                } else if (!findEnum(mappingInstruction.get(i).get(1))) {
                    throw new TokenizerException.BadCharacter('0');
                }

                labels.put(mappingInstruction.get(i).get(0), values);
                mappingInstruction.get(i).remove(0);
            }
        }
    }

}
