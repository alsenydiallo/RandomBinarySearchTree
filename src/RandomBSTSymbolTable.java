import java.util.Random;
import java.util.Vector;


public class RandomBSTSymbolTable <K extends Comparable<K>,V> implements SymbolTable<K, V> {


    private class Node {
        public K key; 
        public V val;
        public Node left, right;
        public int N ;
        public Node(K k, V v) {
            key = k;
            val = v;
            N = 1;
            left = right = null;
        }
    }
    
    private Node root;
    public RandomBSTSymbolTable(){root = null;}
    
	public Vector<String> serialize() {	
		Vector<String> vec = new Vector<String>();
		serializeAux(root, vec);
		return vec;
	}
	
	private void serializeAux(Node tree, Vector<String> vec){
		if(tree == null)
			vec.addElement(null);
		else{
			vec.addElement(tree.key.toString() + ":black");
			serializeAux(tree.left, vec);
			serializeAux(tree.right, vec);
		}
	}
	
	public int size(){
		return (root == null) ? 0 : max(size(root.right), size(root.left)) + 1;
	}
	private int size(Node tree){
		return (tree == null) ? 0 : tree.N;
	}
	
	public boolean isEmpty(){
		return (root == null) ? true : false;
	}
	
	private int max(int x, int y){
		return (x > y) ? x : y;
	}
	
	public Node findMin(Node tree){
		if(tree == null)
			return tree;
		while(tree.left != null)
			tree = tree.left;
		return tree;
	}
	
	public Node findMax(Node tree){
		if(tree == null)
			return tree;
		while(tree.right != null)
			tree = tree.right;
		return tree;
	}
	
	private Node rightRotate(Node tree){
		if(tree != null && tree.left != null){
			Node root = tree.left;
			tree.left = root.right;
			root.right = tree;
			tree.N = size(tree.left) + size(tree.right) + 1;
			root.N = size(root.left) + size(root.right) + 1;
			return root;
		}
		return tree;
	}
	
	private Node leftRotate(Node tree){	
		if(tree != null && tree.right != null){
			Node root = tree.right;
			tree.right = root.left;
			root.left = tree;
			tree.N = size(tree.left) + size(tree.right) + 1;
			root.N = size(root.left) + size(root.right) + 1;
			return root;
		}
		return tree;
	}

	@Override
	public void insert(K key, V val) {
		root = insertRandom(root, key, val);
	}

	static Random rng = new Random(1234);
	
	private Node insertRandom(Node tree, K key, V val){
		if(tree == null) return new Node(key, val);
		
		if(rng.nextDouble() * (tree.N+1) < 1.0)
			return insertRoot(tree, key, val);
		
		int cmp = key.compareTo(tree.key);
		if (cmp == 0){
			tree.key = key;
			tree.val = val;
		} 
		else if(cmp < 0){
			tree.left = insertRandom(tree.left, key, val);
			tree.N = 1 + size(tree.left) + size(tree.right);
		} 
		else{
			tree.right = insertRandom(tree.right, key, val);
			tree.N = 1 + size(tree.left) + size(tree.right);
		} 
		return tree;
	}
	
	private Node insertRoot(Node tree, K key, V val)
	{
		if(tree == null)
			return new Node(key, val);
		
		int cmp = key.compareTo(tree.key);
		if(cmp == 0){ 
			tree.key = key; 
			tree.val = val;
		}
		else if(cmp < 0){
			tree.left = insertRoot(tree.left, key, val);
			tree = rightRotate(tree);
		}
		else if(cmp > 0){
			tree.right = insertRoot(tree.right, key, val);
			tree = leftRotate(tree);
		}
		return tree;
	}
	
	@Override
	public V search(K key) {
		Node tmp = searchAux(root, key);
		return (tmp == null) ? null :tmp.val;
	}

	private Node searchAux(Node tree, K key)
	{
		if(tree == null)
			return null;
		int cmp = key.compareTo(tree.key);
		if(cmp == 0)
			return tree;
		if(cmp < 0)
			return searchAux(tree.left, key);
		return searchAux(tree.right, key);
	}
	
	@Override
	public V remove(K key) {
		Node wacked = new Node(null, null);
		root = removeAux(root, key, wacked);
		return  wacked.val;
	}

	private Node join(Node X, Node Y){
		if(X == null) return Y;
		if(Y == null) return X;
		if(rng.nextDouble() * (X.N + Y.N) < X.N){
			X.right = join(X.right, Y);
			X.N = 1+ size(X.left) + size(X.right);
			return X;
		}else {
			Y.left = join(X, Y.left);
			Y.N = 1 + size(Y.left) + size(Y.right);
			return Y;
		}
	}

	// Reference "Algorithm in C++"
	private Node removeAux(Node tree, K key, Node wacked){
		if(tree == null)
			return null;
		int cmp = key.compareTo(tree.key);
		if(cmp == 0){
			wacked.key = tree.key;
			wacked.val = tree.val;
			return join(tree.left, tree.right);
		}
		else if(cmp < 0){
			tree.left = removeAux(tree.left, key, wacked);
		}
		else{
			tree.right = removeAux(tree.right, key, wacked);
		}
		return tree;
	}

}
