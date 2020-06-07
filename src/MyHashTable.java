import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MyHashTable {
    private int size;
    private HashFunctionRan hashFunction;
    private LinkedNode[] table;
    private int length=0;
    private int bytes=0;

    public MyHashTable(int size) {
    	hashFunction=new HashFunctionRan(size);
        this.size=hashFunction.prime;
        table = new LinkedNode[this.size];
    }

    public void add(String key, String value){
        int index= hashFunction.hash(key);
        LinkedNode rootNode=table[index];
        if (rootNode == null){
            table[index]=new LinkedNode(key, value);
        } else{
            rootNode.addToLast(key, value);
        }
        length++;
        bytes+=(8 * (int) ((((value.length()) * 2) + 45) / 8));
        bytes+=(8 * (int) ((((key.length()) * 2) + 45) / 8));
    }

    public void add(String key){
        String value="";
        int index= hashFunction.hash(key);
        LinkedNode rootNode=table[index];
        if (rootNode == null){
            table[index]=new LinkedNode(key, value);
        } else{
            rootNode.addToLast(key, value);
        }
        length++;
        bytes+=(8 * (int) ((((key.length()) * 2) + 45) / 8));
    }

    public accessStringRet find(String key){
        int index= hashFunction.hash(key);
        LinkedNode rootNode=table[index];
        if (rootNode == null){
            return new accessStringRet(null, 1);
        } else{
            accessStringRet ret = rootNode.find(key);
            while (rootNode.hasNext()) {
            	rootNode = rootNode.getNext();
            	if (rootNode.getKey().equals(key))
            		ret.val = ret.val + "-~:" + rootNode.getValue();
            }
            return ret;
        }
    }

    public int length(){return length;}

    public long memoryBytes(){
        return bytes;
    }
}

//// Only read methods are implemented
//class LinkedNodeIterator{
//    LinkedNode nextNode;
//
//    LinkedNodeIterator(LinkedNode node){
//       nextNode=node;
//    }
//
//    public boolean hasNext(){
//        return nextNode.hasNext();
//    }
//
//    public String nextValue(){
//        nextNode=nextNode.getNext();
//        return nextNode.getData();
//    }
//
//}

class LinkedNode{
    private String key;
    private String value;
    private LinkedNode next;

    public LinkedNode(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setNext(LinkedNode next) {
        this.next = next;
    }

    public LinkedNode getNext() {
        return next;
    }

    public boolean hasNext(){
        return !(next==null);
    }

//    public LinkedNodeIterator iterator(){
//        return new LinkedNodeIterator(this);
//    }

    // a node behaves like a list
    public LinkedNode addToLast(String key, String value){
        LinkedNode node=this;
        while (node.hasNext()){
            node=node.getNext();
        }
        LinkedNode newnode = new LinkedNode(key, value);
        node.setNext(newnode);
        return newnode;
    }

    public accessStringRet find(String key){
        LinkedNode node=this;
        int accessCnt=0;
        if (node.getKey().equals(key)) {
            accessCnt+=1;
            accessStringRet ret= new accessStringRet(node.getValue(), accessCnt);
            return ret;
        }
        while (node.hasNext()){
            node=node.getNext();
            accessCnt+=1;
            if (node.getKey().equals(key)) {
                accessStringRet ret= new accessStringRet(node.getValue(), accessCnt);
                return ret;
            }
        }
        return new accessStringRet(null, accessCnt);
    }
    
}

class accessStringRet {
    String val;
    int access;

    public accessStringRet(String ret, int access) {
        this.val = ret;
        this.access = access;
    }
}
//
//class HashFunctionRan extends HashFunction {
//	int prime;
//	int a;
//	int b;
//
//	public HashFunctionRan(int size) {
//		prime = getPrime(size);
//		a = ThreadLocalRandom.current().nextInt(0, prime);
//		b = ThreadLocalRandom.current().nextInt(0, prime);
//	}
//
//	public int hash(String s) {
//		return hash(s.hashCode());
//	}
//
//	private int hash(int x) {
//		return mod((a * x) + b, prime);
//	}
//
//	/**
//	 * get prime number bigger than n
//	 *
//	 * @param n
//	 * @return boolean
//	 */
//	private int getPrime(int n) {
//		boolean found = false;
//
//		while (!found) {
//			if (isPrime(n)) {
//				found = true;
//			} else {
//				if (n == 1 || n % 2 == 0) {
//					n = n + 1;
//				} else {
//					n = n + 2;
//				}
//			}
//		}
//		return n;
//	}
//
//	/**
//	 * return true if inputNum is prime
//	 *
//	 * @param inputNum
//	 * @return boolean
//	 */
//	private boolean isPrime(int inputNum) {
//		if (inputNum <= 3 || inputNum % 2 == 0)
//			return inputNum == 2 || inputNum == 3;
//		int divisor = 3;
//		while ((divisor <= Math.sqrt(inputNum)) && (inputNum % divisor != 0))
//			divisor += 2;
//		return inputNum % divisor != 0;
//	}
//}
//
