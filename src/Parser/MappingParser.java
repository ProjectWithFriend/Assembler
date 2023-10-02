package Parser;

import java.util.ArrayList;
import java.util.HashMap;

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
    private final ArrayList<String> binaryCode;
    private final ArrayList<Integer> decimalCode;
    private final HashMap<String, ArrayList<Integer>> labels;
    private final HashMap<String, String> opcodeTable = new HashMap<>();
    private final String Bit31_25 = "0000000";

    public MappingParser(ArrayList<ArrayList<String>> mappingInstruction) {
        this.mappingInstruction = mappingInstruction;
        binaryCode = new ArrayList<>();
        decimalCode = new ArrayList<>();
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
    public String PrintCode() {
        StringBuilder p = new StringBuilder();
        MappingInstruction();
        System.out.println("Binary Code:");
        for(String code : binaryCode){
            decimalCode.add((int) Long.parseLong(code,2));
            System.out.println(code);
        }
        System.out.println("Decimal Code:");
        for(int code: decimalCode){
            p.append(code);
            p.append("\n");
            System.out.println(code);
        }
        return p.toString();
    }

    private void MappingInstruction() {
        CreateLabelsTable();
        int line = 0;
        for (ArrayList<String> instructions : mappingInstruction) {
            line++;
            String binary;
            if (!instructions.isEmpty()) { // to check is in not case last member is empty list
                if (findEnum(instructions.get(0))) {
                    String instruction = instructions.get(0);
                    String Bit24_22 = opcodeTable.get(instructions.get(0));
                    binary = "";
                    try{
                        switch (instruction) {
                            case "add", "nand" -> //R-type
                                    binary = R_type(instructions, Bit24_22);
                            case "lw", "sw", "beq" -> //I-type
                                    binary = I_type(instructions, Bit24_22, line, instruction.equals("beq"));
                            case "jalr" -> //J-type
                                    binary = J_type(instructions, Bit24_22);
                            case "halt", "noop" -> //O-type
                                    binary = Bit31_25 + Bit24_22 + "0000000000000000000000";
                        }
                    }catch (Exception e) {
                        System.exit(1);
                    }
                } else {//.fill case
                    if(isNum(instructions.get(1))){
                        binary = ExtendTo32Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(1))));
                    }else{
                        binary = ExtendTo32Bit(Integer.toBinaryString(labels.get(instructions.get(1)).get(0)));
                    }
                }
                binaryCode.add(binary);
            }
        }
    }

    private String R_type(ArrayList<String> instructions, String Bit24_22){
        StringBuilder code = new StringBuilder(Bit31_25);
        code.append(Bit24_22);
        for(int i = 1; i <= 3; i++){
            if(i == 3) code.append("0000000000000");
            if(isNum(instructions.get(i))){
                if(Integer.parseInt(instructions.get(i)) > 7)
                    System.exit(1);
                code.append(ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(i)))));
            }else{
                if(labels.get(instructions.get(i)).get(0) > 7)
                    System.exit(1);
                int line_label = labels.get(instructions.get(i)).get(0);
                code.append(ExtendTo3Bit(Integer.toBinaryString(line_label)));
            }
        }
        return code.toString();
    }

    private String I_type(ArrayList<String> instructions, String Bit24_22, int line, boolean is_beq){
        StringBuilder code = new StringBuilder(Bit31_25);
        code.append(Bit24_22);
        for(int i = 1; i <= 3; i++){
            if(isNum(instructions.get(i))){
                if(i != 3) {
                    if (Integer.parseInt(instructions.get(i)) > 7)
                        System.exit(1);
                    code.append(ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(i)))));
                }else{
                    if(is_beq){
                        int offset = Integer.parseInt(instructions.get(i));
                        String off = ExtendTo16Bit(Integer.toBinaryString(offset));
                        code.append(ExtendTo16Bit(off));
                    }else{
                        code.append(ExtendTo16Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(i)))));
                    }
                }
            }else{
                if(i != 3){
                    if(labels.get(instructions.get(i)).get(0) > 7)
                        System.exit(1);
                    code.append(ExtendTo3Bit(Integer.toBinaryString(labels.get(instructions.get(i)).get(0))));
                }else{
                    if(is_beq){
                        int offset = labels.get(instructions.get(i)).get(0) - line;
                        String off = ExtendTo16Bit(Integer.toBinaryString(offset));
                        code.append(ExtendTo16Bit(off));
                    }else{
                        int offset = labels.get(instructions.get(i)).get(0) - line < 0 ?
                                labels.get(instructions.get(i)).get(0) + line :
                                labels.get(instructions.get(i)).get(0);
                        code.append(ExtendTo16Bit(Integer.toBinaryString(offset)));
                    }
                }
            }
        }
        return code.toString();
    }

    private String J_type(ArrayList<String> instructions, String Bit24_22){
        StringBuilder code = new StringBuilder(Bit31_25);
        code.append(Bit24_22);
        for(int i = 1; i <= 2; i++){
            if(isNum(instructions.get(i))){
                if(Integer.parseInt(instructions.get(i)) > 7)
                    System.exit(1);
                code.append(ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(i)))));
            }else{
                if(labels.get(instructions.get(i)).get(0) > 7)
                    System.exit(1);
                int line_label = labels.get(instructions.get(i)).get(0);
                code.append(ExtendTo3Bit(Integer.toBinaryString(line_label)));
            }
        }
        code.append("0000000000000000");
        return code.toString();
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

    private boolean isNum(String strNum) {
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void CreateLabelsTable() {
        for (int i = 0; i < mappingInstruction.size() - 1; i++) {
            if (!findEnum(mappingInstruction.get(i).get(0))) {//if to check is label?
                String word = mappingInstruction.get(i).get(0);
                if(word.length() > 6) System.exit(1);
                if (Character.isDigit(word.charAt(0)) || word.equals(".fill")) {
                    // if check label start with number or .fill
                    System.exit(1);
                }

                ArrayList<Integer> values = new ArrayList<>();
                values.add(i); // add # line

                if (mappingInstruction.get(i).get(1).equals(".fill")) {//in this case to check is .fill case
                    try {
                        values.add(Integer.parseInt(mappingInstruction.get(i).get(2)));
                    } catch (Exception e) {
                        if (labels.get(mappingInstruction.get(i).get(2)) == null) {
                            String keyword = mappingInstruction.get(i).get(2);
                            for(int j = i; j < mappingInstruction.size() - 1; j++){
                                if(mappingInstruction.get(j).get(0).equals(keyword)){
                                    values.add(j);
                                    break;
                                }
                                if(j == mappingInstruction.size() - 2){
                                    System.exit(1);
                                }
                            }
                        } else {
                            int line = labels.get(mappingInstruction.get(i).get(2)).get(0);
                            values.add(line);
                        }
                    }
                } else if (!findEnum(mappingInstruction.get(i).get(1))) {
                    System.exit(1);
                }

                labels.put(mappingInstruction.get(i).get(0), values);
                mappingInstruction.get(i).remove(0);
            }
        }
    }
}
