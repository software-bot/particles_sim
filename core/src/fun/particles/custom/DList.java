package fun.particles.custom;

class DList<T> {

    static class Node<T> {
        T val;
        Node<T> next;
        Node<T> prev;

        Node(T val) {
            this.val = val;
        }
    }

    private Node<T> head, tail;

    DList() {
        head = new Node<>(null);
        tail = new Node<>(null);

        head.next = tail;
        tail.prev = head;
    }

    Node<T> first() {
        return head.next;
    }

    void remove(Node node) {
        Node p = node.prev;
        Node n = node.next;

        p.next = n;
        n.prev = p;
    }

    void add(T val) {
        Node<T> n = new Node<>(val);

        head.next.prev = n;
        n.next = head.next;
        head.next = n;
        n.prev = head;
    }
}
