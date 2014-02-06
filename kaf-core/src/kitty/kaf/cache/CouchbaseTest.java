package kitty.kaf.cache;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.couchbase.client.CouchbaseClient;

public class CouchbaseTest {
	static CouchbaseClient client;

	public static void main(String[] args) {
		new CouchbaseTest();
	}

	public CouchbaseTest() {
		try {
			List<URI> hosts = Arrays.asList(new URI("http://127.0.0.1:8091/pools"));

			// Name of the Bucket to connect to
			String bucket = "default";

			// Password of the bucket (empty) string if none
			String password = "";

			// Connect to the Cluster
			client = new CouchbaseClient(hosts, bucket, password);
			// Store a Document
			// client.set("my-first-document", "Hello Couchbase!").get();

			// Retreive the Document and print it
			for (int i = 0; i < 100; i++) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (int j = 0; j < 100; j++) {
							try {
								client.set("my-first-document", new Date().toString()).get();
								System.out.println(new Date() + ":" + Thread.currentThread().getName() + "," + ") -> "
										+ client.get("aabbcc"));
								System.out.println(new Date() + ":" + Thread.currentThread().getName() + "," + j
										+ ") -> " + client.get("my-first-document"));
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}
				}, "thread-" + i).start();
			}
			// Shutting down properly
			// client.shutdown();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
