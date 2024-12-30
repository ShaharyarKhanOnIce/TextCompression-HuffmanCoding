import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class HuffmanCoding {
    private static Map<Character, String>      // maps for encoding and decoding
    huffmanCodes = new HashMap<>();
    private static Map<String, Character>
    reverseHuffmanCodes = new HashMap<>();

    // Huffman Coding Methods

    private static Node buildHuffmanTree(Map<Character, Integer> freqMap) {      // builds Huffman Tree based on frequencies
        PriorityQueue<Node>
        priorityQueue = new
        PriorityQueue<>(new NodeComparator());      // Min heap based on frequency
        for                                                            
        (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            priorityQueue.add(new Node(entry.getKey(), entry.getValue()));          
        } 
        while (priorityQueue.size() > 1) {             // pairs nodes... until one root node remains (hence while loop)
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();
            Node parent = new Node('\0', left.freq + right.freq);   
            parent.left = left;
            parent.right = right;

            priorityQueue.add(parent);
        }
        return priorityQueue.poll();       // returns root of tree
    }

    private static void generateCodes(Node root, String code) {     // the name is pretty self explanatory (generate codes for each node)
        if (root == null) {
            return; }
        if (root.left == null && root.right == null) {
            huffmanCodes.put(root.character, code);
            reverseHuffmanCodes.put(code, root.character);
        }

        generateCodes(root.left, code + "0");
        generateCodes(root.right, code + "1");
    }

    private static Map<Character, Integer> calculateFreq(String text) {        // calculates frequency of each character in input text (to encode)
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        return freqMap;
    }

    private static String encode(String text) {                   // encode and decode methods
        StringBuilder encodedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            encodedText.append(huffmanCodes.get(c));
        }

        return encodedText.toString();
    }

    private static String decode(String encodedText) {
        StringBuilder decodedText = new StringBuilder();
        StringBuilder tempCode = new StringBuilder();

        for (char bit : encodedText.toCharArray()) {
            tempCode.append(bit);

            if (reverseHuffmanCodes.containsKey(tempCode.toString())) {
                decodedText.append(reverseHuffmanCodes.get(tempCode.toString()));

                tempCode.setLength(0);
            }
        }

        return decodedText.toString();
    }

    //File Handling Methods

    private static String readFile(String fileName) throws IOException {           // read file method
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
        }

        return content.toString().trim();
    }

    private static void writeFile(String fileName, String content) throws IOException {      // write file method

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        }
    }

    // Main Method

    public static void main(String[] args) {
        Scanner readln = new Scanner(System.in);

        System.out.println("Do you want to encode or decode text? (Enter 'encode' or 'decode')"); 
        String actionToPerform = readln.nextLine().toLowerCase();

        try {
            if (actionToPerform.equals("encode")) {
                System.out.println("Enter the text to encode or the name of a file (.txt) containing the text:");
                String input = readln.nextLine();

                String text = input.endsWith(".txt") ? readFile(input) : input;  // ternary operator (note to self: check if these exist in Kotlin)

                Map<Character, Integer> frequencyMap = calculateFreq(text); 
                Node root = buildHuffmanTree(frequencyMap); 
                generateCodes(root, ""); 

                String encodedText = encode(text); 

                System.out.println("Encoded Text: " + encodedText);

                System.out.println("Do you want to save the encoding map to a file? (yes/no)");
                if (readln.nextLine().equalsIgnoreCase("yes")) {
                    System.out.println("Enter the file name to save the map:");
                    String mapFileName = readln.nextLine();
                    writeFile(mapFileName, huffmanCodes.toString());
                }

                System.out.println("Do you want to save the encoded text to a file? (yes/no)");
                if (readln.nextLine().equalsIgnoreCase("yes")) {
                    System.out.println("Enter the file name to save the encoded text:");
                    String encodedFileName = readln.nextLine();
                    writeFile(encodedFileName, encodedText.toString());
                }

            } else if (actionToPerform.equals("decode")) {
                System.out.println("Enter the encoded text or the name of a file (.txt) containing the encoded text:");
                String input = readln.nextLine();

                String encodedText = input.endsWith(".txt") ? readFile(input) : input;

                System.out.println("Enter the key (encoding map) or the name of a file (.txt) containing the key:");
                String keyInput = readln.nextLine();

                String keyData = keyInput.endsWith(".txt") ? readFile(keyInput) : keyInput;

                keyData = keyData.replaceAll("[{}]", "");      // reads file contaning info about key (code of each character)
                for (String entry : keyData.split(", ")) {                 // + avoids things not of use but uses them as markers
                    String[] pair = entry.split("=");
                    reverseHuffmanCodes.put(pair[1], pair[0].charAt(0));
                }

                String decodedText = decode(encodedText);
                System.out.println("Decoded Text: " + decodedText);
            } else {                                                      // handling errors
                System.out.println("Invalid input. Please restart the program and enter 'encode' or 'decode'.");
            }
        } catch (IOException e) {
            System.err.println("Error handling file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

        readln.close();                     // closes scanner
    }

    }
