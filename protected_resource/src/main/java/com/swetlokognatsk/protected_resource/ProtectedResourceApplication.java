package com.swetlokognatsk.protected_resource;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.swetlokognatsk.protected_resource.ports.Database;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// TODO what is `nextval('words_id_seq'::regclass)`???
// Column |          Type          | Collation | Nullable |              Default              
// --------+------------------------+-----------+----------+-----------------------------------
// id     | integer                |           | not null | nextval('words_id_seq'::regclass)

// TODO is it possible to configure vs code to run all 4 debuggers
@SpringBootApplication
@EntityScan(basePackages = { "com.swetlokognatsk.protected_resource", "com.swetlokognatsk.oauth_db.models" })
@RestController
public class ProtectedResourceApplication {

	private static final String HOME = "/";
	private static ApplicationContext ctx;

	public static void main(String[] args) {
		ctx = SpringApplication.run(ProtectedResourceApplication.class, args);
	}

	@GetMapping(HOME)
	public String home() {
		return "<h1>Hello ProtectedResourceApplication</h1>";
	}

	@RequestMapping("/resource/fetch")
	// https://stackoverflow.com/questions/60671020/how-to-get-spring-boot-to-map-query-parameters-separately-from-form-data
	// in prod only one way of getting accessToken must be implemented. whereas here may be collisions, though it works well for any request
	public ResponseEntity<String> fetchProtectedResource() {
		return ResponseEntity.ok("BAZINGA.PNG");
	}

	@GetMapping("/words")
	public ResponseEntity<String> getWords() {
		var db = getDatabase();
		var words = db.getWords();
		var wordsString = words.stream().reduce((resultString, word) -> "%s %s".formatted(resultString, word)).orElse("");
		return ResponseEntity.ok(wordsString);
	}

	@PostMapping("/words")
	public ResponseEntity<String> addWord(@RequestBody final MultiValueMap<String, String> bodyParams) {
		var db = getDatabase();
		var newWord = bodyParams.get("newWord").getFirst();
		try {
			db.addWord(newWord);
			return ResponseEntity.status(201).build();
		} catch (EntityExistsException e) {
			return ResponseEntity.status(409).build();
		}
	}

	@DeleteMapping("/words")
	public ResponseEntity<String> removeWord(@RequestParam final String wordToDelete) {
		var db = getDatabase();
		try {
			db.removeWord(wordToDelete);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.status(204).build();
	}

	private static Database getDatabase() {
		return ctx.getBean(Database.class);
	}

}
