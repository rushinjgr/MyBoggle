/**
 * Created by wille on 9/5/14.
 */

public class DLBDictionary implements DictionaryInterface{

    TrieNode root = new TrieNode();

    public boolean add(String s){
        boolean result = false;
        char[] target = s.toCharArray();
        TrieNode current = root;
        TrieNode next = null;
        for (char t: target){
            next = current.chars.get(t);
            if(next == null){
                next = current.chars.add(t);
            }
            else{

            }
            current = next;
        }
        //mark as terminating
        next.value = 1;
        return true;
    }


    //0 => not a word or prefix
    //1 => prefix not word
    //2 => word not prefix
    //3 => both
    public int search2(StringBuilder s){
        char target;
        TrieNode current = root;
        TrieNode next;
        boolean prefix = false;
        boolean word = false;
        for (int i = 0; i < s.length(); i++) {
            target = s.charAt(i);
            next = current.chars.get(target);
            if(next != null){
                current = next;
            }
            //dead end
            else{
                return 0;
            }
        }
        //word is a prefix
        if(current.chars.size > 0){
            prefix = true;
        }
        if(current.value >0){
            word = true;
        }
        if(word){
            if (prefix){
                return 3;
            }
            else{
                return 2;
            }
        }
        else{
            return 1;
        }
    }

    public int search(StringBuilder s){
        boolean result = false;
        char[] target = s.toString().toCharArray();
        TrieNode current = root;
        TrieNode next = null;
        boolean word = false;
        boolean prefix = false;
        for (char t: target){
            next = current.chars.get(t);
            if(next == null){
                //dead end
                return 0;
            }
            else {
                current = next;
            }
        }
        if(current.value > 0){
            word = true;
        }
        if (current.chars.size>0){
            prefix = true;
        }

        if(word){
            if (prefix){
                return 3;
            }
            else{
                return 2;
            }
        }
        else{
            return 1;
        }
    }
    private class CharNode{
        public char value;
        public TrieNode node;
        public CharNode next;
    }


    //a linked list of charNodes
    private class CharList{

        private CharNode first;
        private int size;

        public TrieNode add(char newChar){
            CharNode newNode = new CharNode();
            newNode.value = newChar;
            newNode.node = new TrieNode();
            //case 1: first is null
            if(first == null){
                first = newNode;
                size++;
            }
            //case 2: newChar is before first
            else if(newChar < first.value){
                newNode.next = first;
                first = newNode;
                size++;
            }
            //case 3: newChar is the same as the first
            else if(newChar == first.value){
            }
            //newChar is after first
            else{
                CharNode current = first;
                boolean inserted = false;
                while(!inserted){
                    //case 4 there is no next node
                    if(current.next == null){
                        current.next = newNode;
                        inserted = true;
                        size++;
                    }
                    //case 5 new node goes between current and next
                    else if(current.value > newChar){
                        newNode.next = current.next;
                        current.next = newNode;
                        inserted = true;
                        size++;
                    }
                    else if (current.value == newChar){
                        inserted = true;
                    }
                    //case 7 new char is greater than next
                    else{
                        inserted = false;
                        current = current.next;
                    }
                }
            }
            return newNode.node;
        }

        public TrieNode get(char val){
            TrieNode result = null;
            CharNode current = first;
            boolean found = false;
            while(current != null && found == false){
                if(current.value == val){
                    result = current.node;
                    found = true;
                }
                else{
                    current = current.next;
                }
            }
            return result;
        }
    }

    private class TrieNode{
        public int value;
        public CharList chars;

        public TrieNode(){
            value = 0;
            chars = new CharList();
        }
    }

}
