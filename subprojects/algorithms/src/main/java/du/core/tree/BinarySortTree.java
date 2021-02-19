package du.core.tree;

/**
 * 二叉排序树（二叉搜索树、二叉查找树）
 *
 * @author ddq
 */
public class BinarySortTree<E extends Comparable> {

    private TreeNode<E> root;

    private int size;

    public boolean add(E e){
        if(root == null){
            root = new TreeNode<E>(e);
            size = 1;
            return true;
        }
        return this.add(this.root, e);
    }

    public boolean add(TreeNode<E> node, E e){

        int cmp = e.compareTo(node.getElement());

        if(cmp < 0){
            if(node.getLeftChild() == null){
                TreeNode<E> newNode = new TreeNode<>(e);
                newNode.setParent(node);
                node.setLeftChild(newNode);
                return true;
            }
            return add(node.getLeftChild(), e);
        }
        else if(cmp > 0) {
            if(node.getRightChild() == null){
                TreeNode<E> newNode = new TreeNode<E>(e);
                newNode.setParent(node);
                node.setRightChild(newNode);
                return true;
            }
            return add(node.getRightChild(), e);
        }
        else{
            return false;
        }
    }

    public E find(E e){
        return find(root, e);
    }

    private E find(TreeNode<E> node, E e){

        if(node == null)
            return null;

        int cmp = e.compareTo(node.getElement());

        if(cmp < 0){
            return find(node.getLeftChild(), e);
        }
        else if(cmp > 0) {
            return find(node.getRightChild(), e);
        }
        else{
            return node.getElement();
        }
    }


}
