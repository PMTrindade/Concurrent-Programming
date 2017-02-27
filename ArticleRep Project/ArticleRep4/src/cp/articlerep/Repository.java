package cp.articlerep;

import java.util.HashSet;

import cp.articlerep.ds.Iterator;
import cp.articlerep.ds.LinkedList;
import cp.articlerep.ds.List;
import cp.articlerep.ds.Map;
import cp.articlerep.ds.HashTable;

/**
 * @author Ricardo Dias
 */
public class Repository {

	private Map<String, List<Article>> byAuthor;
	private Map<String, List<Article>> byKeyword;
	private Map<Integer, Article> byArticleId;

	public Repository(int nkeys) {
		this.byAuthor = new HashTable<String, List<Article>>(40000);
		this.byKeyword = new HashTable<String, List<Article>>(40000);
		this.byArticleId = new HashTable<Integer, Article>(40000);
	}

	public boolean insertArticle(Article a) {

		byArticleId.singleLock(a.getId());

			if (byArticleId.contains(a.getId())){
				byArticleId.singleUnlock(a.getId());
				return false;
			}



			Iterator<String> authors = a.getAuthors().iterator();
			while (authors.hasNext()) {
				String name = authors.next();

				byAuthor.singleLock(name);
				List<Article> ll = byAuthor.get(name);
				if (ll == null) {
					ll = new LinkedList<Article>();
					byAuthor.put(name, ll);
				}
				ll.add(a);
				byAuthor.singleUnlock(name);
			}

			Iterator<String> keywords = a.getKeywords().iterator();
			while (keywords.hasNext()) {
				String keyword = keywords.next();

				byKeyword.singleLock(keyword);
				List<Article> ll = byKeyword.get(keyword);
				if (ll == null) {
					ll = new LinkedList<Article>();
					byKeyword.put(keyword, ll);
				}
				ll.add(a);
				byKeyword.singleUnlock(keyword);
			}
			byArticleId.put(a.getId(), a);

			byArticleId.singleUnlock(a.getId());

		return true;
	}

	public void removeArticle(int id) {
		byArticleId.singleLock(id);

		Article a = byArticleId.get(id);

		if (a == null){
			byArticleId.singleUnlock(id);
			return;
		}



		Iterator<String> keywords = a.getKeywords().iterator();
		while (keywords.hasNext()) {
			String keyword = keywords.next();

			byKeyword.singleLock(keyword);
			List<Article> ll = byKeyword.get(keyword);
			if (ll != null) {
				int pos = 0;
				Iterator<Article> it = ll.iterator();
				while (it.hasNext()) {
					Article toRem = it.next();
					if (toRem == a) {
						break;
					}
					pos++;
				}
				ll.remove(pos);
				it = ll.iterator();
				if (!it.hasNext()) { // checks if the list is empty
					byKeyword.remove(keyword);
				}
				byKeyword.singleUnlock(keyword);
			}
		}

		Iterator<String> authors = a.getAuthors().iterator();
		while (authors.hasNext()) {
			String name = authors.next();

			byAuthor.singleLock(name);
			List<Article> ll = byAuthor.get(name);
			if (ll != null) {
				int pos = 0;
				Iterator<Article> it = ll.iterator();
				while (it.hasNext()) {
					Article toRem = it.next();
					if (toRem == a) {
						break;
					}
					pos++;
				}
				ll.remove(pos);
				it = ll.iterator();
				if (!it.hasNext()) { // checks if the list is empty
					byAuthor.remove(name);
				}
			}
			byAuthor.singleUnlock(name);
		}

		byArticleId.remove(id);

		byArticleId.singleUnlock(id);
	}

	public List<Article> findArticleByAuthor(List<String> authors) {
		List<Article> res = new LinkedList<Article>();


		Iterator<String> it = authors.iterator();
		while (it.hasNext()) {
			String name = it.next();
			byAuthor.singleLock(name);
			List<Article> as = byAuthor.get(name);
			if (as != null) {
				Iterator<Article> ait = as.iterator();
				while (ait.hasNext()) {
					Article a = ait.next();
					res.add(a);
				}
			}
			byAuthor.singleUnlock(name);
		}

		return res;
	}

	public List<Article> findArticleByKeyword(List<String> keywords) {
		List<Article> res = new LinkedList<Article>();



		Iterator<String> it = keywords.iterator();
		while (it.hasNext()) {
			String keyword = it.next();

			byKeyword.singleLock(keyword);
			List<Article> as = byKeyword.get(keyword);
			if (as != null) {
				Iterator<Article> ait = as.iterator();
				while (ait.hasNext()) {
					Article a = ait.next();
					res.add(a);
				}
			}
			byKeyword.singleUnlock(keyword);
		}


		return res;
	}


	/**
		 * This method is supposed to be executed with no concurrent thread
		 * accessing the repository.
		 */
		public boolean validate() {
			HashSet<Integer> articleIds = new HashSet<Integer>();
			int articleCount = 0;

			Iterator<Article> aIt = byArticleId.values();
			while(aIt.hasNext()) {
				Article a = aIt.next();

				articleIds.add(a.getId());
				articleCount++;

				// check the authors consistency
				Iterator<String> authIt = a.getAuthors().iterator();
				while(authIt.hasNext()) {
					String name = authIt.next();
					if (!searchAuthorArticle(a, name)) {
						return false;
					}
				}

				// check the keywords consistency
				Iterator<String> keyIt = a.getKeywords().iterator();
				while(keyIt.hasNext()) {
					String keyword = keyIt.next();
					if (!searchKeywordArticle(a, keyword)) {
						return false;
					}
				}
			}

			// check the articles consistency inside the authors hash map
			Iterator<String> authNamesIt = byAuthor.keys(); // Implemented keys iterator
			while(authNamesIt.hasNext()) {
				String name = authNamesIt.next();
				if (!checkAuthorArticles(name)) { // Implemented auxiliary method
					return false;
				}
			}

			// check the articles consistency inside the keywords hash map
			Iterator<String> keywordsIt = byKeyword.keys(); // Implemented keys iterator
			while(keywordsIt.hasNext()) {
				String word = keywordsIt.next();
				if (!checkKeywordArticles(word)) { // Implemented auxiliary method
					return false;
				}
			}

			if(!byArticleId.validate() || !byAuthor.validate() || !byKeyword.validate()) { // Implemented  validate method
				return false;
			}

			return articleCount == articleIds.size();
		}

		private boolean searchAuthorArticle(Article a, String author) {
			List<Article> ll = byAuthor.get(author);
			if (ll != null) {
				Iterator<Article> it = ll.iterator();
				while (it.hasNext()) {
					if (it.next() == a) {
						return true;
					}
				}
			}

			return false;
		}

		private boolean searchKeywordArticle(Article a, String keyword) {
			List<Article> ll = byKeyword.get(keyword);
			if (ll != null) {
				Iterator<Article> it = ll.iterator();
				while (it.hasNext()) {
					if (it.next() == a) {
						return true;
					}
				}
			}

			return false;
		}

		// Auxiliary method added to check if a given author's articles are all valid (all exist in the articles hash map)
		private boolean checkAuthorArticles(String author) {
			List<Article> ll = byAuthor.get(author);
			if (ll != null) {
				Iterator<Article> it = ll.iterator();
				while (it.hasNext()) {
					if (!byArticleId.contains(it.next().getId())) {
						return false;
					}
				}
			}

			return true;
		}

		// Auxiliary method added to check if a given keyword's articles are all valid (all exist in the articles hash map)
		private boolean checkKeywordArticles(String keyword) {
			List<Article> ll = byKeyword.get(keyword);
			if (ll != null) {
				Iterator<Article> it = ll.iterator();
				while (it.hasNext()) {
					if (!byArticleId.contains(it.next().getId())) {
						return false;
					}
				}
			}

			return true;
		}
}
