import structs.HashTable;

func main(Int argc, String[] argv) {
	HashTable ht = new;
	
	/* Testing contains method */
	printf("Contains foo? %s\n", ht.contains("foo") ? "yes" : "no");
	
	/* Test put/contains/get */
	printf("Adding foo -> bar\n");
	ht.put("foo", "bar");
	printf("Contains foo now? %s\n", ht.contains("foo") ? "yes" : "no");
	printf("%s\n", (String)ht.get("foo"));
	
	/* Test overwriting with put */
	printf("Overwriting foo with baz\n");
	ht.put("foo", "baz");
	printf("%s\n", (String)ht.get("foo"));
	
	/* Try to resize the table, see if it worked */
	printf("Resizing table\n");
	ht.resize(50);
	printf("Still contains foo? %s\n", ht.contains("foo") ? "yes" : "no");
	
	/* Add a few keys */
	printf("Adding keys bar, baz, quux\n");
	ht.put("bar", "bar");
	ht.put("baz", "baz");
	ht.put("quux", "quux");
	
	/* Test removal */
	printf("Removing foo\n");
	ht.remove("foo");
	printf("Contains foo now? %s\n", ht.contains("foo") ? "yes" : "no");
	
	/* Check out the keys */
	printf("Keys: ");
	Iterator key_iter = ht.keys.iterator();
	while (key_iter.hasNext()) {
		String key = key_iter.next();
		printf("%s=%s ", key, (String)ht.get(key));
	}
	printf("\n");
	
}
