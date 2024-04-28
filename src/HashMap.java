public class HashMap {
    public int size() {
        return size;
    }

    public void remove(ServiceType service) {
        int bucketIndex = getBucketIndex(service);
        HashNode head = buckets[bucketIndex];
        HashNode prev = null;

        while (head != null) {
            if (head.key.equals(service)) {
                break;
            }
            prev = head;
            head = head.next;
        }

        if (head == null) {
            return;
        }

        size--;

        if (prev != null) {
            prev.next = head.next;
        } else {
            buckets[bucketIndex] = head.next;
        }
    }

    private class HashNode {
        ServiceType key;
        ArrayList<Place> value;
        HashNode next;

        public HashNode(ServiceType key, ArrayList<Place> value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private HashNode[] buckets;
    private int capacity;
    private int size;

    public HashMap(int capacity) {
        this.capacity = capacity;
        this.buckets = new HashNode[capacity];
        this.size = 0;
    }

    private int getBucketIndex(ServiceType key) {
        int hashCode = key.hashCode();
        return Math.abs(hashCode) % capacity;
    }

    public void put(ServiceType key, Place value) {
        int bucketIndex = getBucketIndex(key);
        HashNode head = buckets[bucketIndex];

        while (head != null) {
            if (head.key.equals(key)) {
                if (!head.value.get(value)) {
                    head.value.add(value);
                }
                return;
            }
            head = head.next;
        }

        size++;
        head = buckets[bucketIndex];
        HashNode newNode = new HashNode(key, new ArrayList<>());
        newNode.value.add(value);
        newNode.next = head;
        buckets[bucketIndex] = newNode;
    }

    public ArrayList<Place> get(ServiceType key) {
        int bucketIndex = getBucketIndex(key);
        HashNode head = buckets[bucketIndex];

        while (head != null) {
            if (head.key.equals(key)) {
                return head.value;
            }
            head = head.next;
        }
        return null;
    }
}
