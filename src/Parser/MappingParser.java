package Parser;


import Tokenizer.TokenizerException;

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
        for(ArrayList<String> instructions: mappingInstruction){
            if(findEnum(instructions.get(0))){
                String instruction = instructions.get(0);
                String Bit24_22 = opcodeTable.get(instructions.get(0));
                if(instruction.equals("add") || instruction.equals("nand")){
                    try{
                        String Bit21_19 = ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(1))));
                        String Bit18_16 = ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(2))));
                        String Bit15_3 = "0000000000000";
                        String Bit2_0 = ExtendTo3Bit(Integer.toBinaryString(Integer.parseInt(instructions.get(3))));
                        String binary = Bit31_25 + Bit24_22 + Bit21_19 + Bit18_16 + Bit15_3 + Bit2_0;
                        binaryCode.add(binary);
                    }catch(NumberFormatException e){
                        System.exit(1);
                    }
                }
            }else{

            }
        }
    }

    private String ExtendTo3Bit(String number){
        StringBuilder numberBuilder = new StringBuilder(number);
        for(int i = numberBuilder.length(); i < 3; i++){
            numberBuilder.insert(0, "0");
        }
        return numberBuilder.toString();
    }

    private void CreateLabelsTable(){
        for(int i = 0; i < mappingInstruction.size() - 1; i++) {
            if(!findEnum(mappingInstruction.get(i).get(0))){//if to check is label?
                String word = mappingInstruction.get(i).get(0);
                if(Character.isDigit(word.charAt(0)) || word.equals(".fill")){// if check label start with number or .fill
                    throw new TokenizerException.BadCharacter(word.charAt(0));
                }

                ArrayList<Integer> values = new ArrayList<>();
                values.add(i); // add # line

                if(mappingInstruction.get(i).get(1).equals(".fill")){//in this case to check is .fill case
                    try{
                        values.add(Integer.parseInt(mappingInstruction.get(i).get(2)));
                    }catch(Exception e){
                        if(labels.get(mappingInstruction.get(i).get(2)) == null){
                            throw new TokenizerException.BadCharacter('0');
                        }else{
                            int line = labels.get(mappingInstruction.get(i).get(2)).get(0);
                            values.add(line);
                        }
                    }
                }

                labels.put(mappingInstruction.get(i).get(0), values);
                mappingInstruction.get(i).remove(0);
            }
        }
    }

}
