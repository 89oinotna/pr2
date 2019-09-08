package progettopr2;

import java.util.Iterator;




public interface SecureDataContainer<E> {
	/*OVERVIEW: Collezione modificabile per la memorizzazione e condivisione di dati di tipo E 
				tramite autenticazione con id e password
	 TYPICAL ELEMENT: <id_0, pw_0, user_space_0>, ...,<id_n-1, pw_n-1, user_space_n-1>
	 				 dove id_i!=id_j per ogni i!=j tc 0<=i<j<n
	 				 	  user_space_i è una coppia <{owned_0, ...,owned_m}, {shared_obj_0, ..., shared_obj_k}> in cui:
	 				 	  			owned_i!=owned_j per ogni i!=j tc 0<=i<j<m
	 				 	  			owned_i è una coppia <obj, {shared_id}> in cui:
	 				 	  			 	obj è un dato non nullo di cui è proprietarioun user
	 				 	  			 	shared_id è una collezione di id con i quali è condiviso obj
									shared_obj_i!=shared_obj_j per ogni i!=j tc 0<=i<j<k	
	 				 	  			shared_obj_i rappresenta un dato non nullo condiviso con l'user
	 				  		  
	*/
	
	// Crea l’identità un nuovo utente della collezione
	public void createUser(String Id, String passw) throws NullPointerException, DuplicateException;
	/*	REQUIRES: id!=null, passw!=null e non esiste id in this
	  	THROWS: se id=null o passw = null throws NullPointerException
	  			se id è gia presente in this throws DuplicateException
	 	MODIFIES: this
	 	EFFECTS: aggiunge a this il nuovo utente
	*/
	 
	
	// Restituisce il numero degli elementi di un utente presenti nella
	// collezione
	public int getSize(String Owner, String passw) throws NullPointerException, IncorrectCredentialsException;
	/*	REQUIRES: owner!=null passw!=null e esiste una coppia <owner, passw>
	  	THROWS:	se id=null o passw=null throw NullPointerException
	  	        se non sono rispettati i controlli d'identità throw IncorrectCredentialsException
		EFFECTS: restituisce il numero di oggetti di cui è proprietario owner
	*/
	
	// Inserisce il valore del dato nella collezione
	// se vengono rispettati i controlli di identità
	public boolean put(String Owner, String passw, E data) throws NullPointerException, IncorrectCredentialsException, DuplicateException;
	/*	REQUIRES: owner!=null, passw!=null, data!=null
  		THROWS: se owner=null o passw = null o data=null throws NullPointerException
  		 		se non sono rispettati i controlli d'identità throw IncorrectCredentialsException
  		        se data è gia presente negli oggetti di cui è proprietario owner throw DuplicateException
 		MODIFIES: this
 		EFFECTS: Aggiunge data agli elementi di cui è proprietario owner e restituisce true
	*/
	
	// Ottiene una copia del valore del dato nella collezione
	// se vengono rispettati i controlli di identità
	public E get(String Owner, String passw, E data) throws NullPointerException, IncorrectCredentialsException, DataNotFoundException;
	/*	REQUIRES: owner!=null, passw!=null, data!=null
		THROWS: se owner=null o passw = null o data=null throws NullPointerException
		 		se non sono rispettati i controlli d'identità throw IncorrectCredentialsException	
				se data non è presente tra gli oggetti di cui Owner è proprietario e quelli condivisi
				   	con lui throw ItemDoesntExist
		EFFECTS: Restituisce una copia dell'oggetto uguale a data presente nella collezione di Owner
	 			cercando prima tra gli oggetti di cui è proprietario e poi tra i condivisi
	 */
	
	// Rimuove il dato nella collezione
	// se vengono rispettati i controlli di identità
	public E remove(String Owner, String passw, E data) throws NullPointerException, IncorrectCredentialsException, DataNotFoundException;
	/*	REQUIRES: owner!=null, passw!=null, data!=null
		THROWS: se owner=null o passw = null o data=null throws NullPointerException
				se non sono rispettati i controlli d'identità throw IncorrectCredentialsException	
				se data non è presente tra gli oggetti di cui è proprietario Owner throw ItemDoesntExist
		MODIFIES: this
		EFFECTS: Rimuove data dagli oggetti di cui è proprietario owner 
			     e restituisce l'elemento rimosso
			  		
	 */
	
	// Crea una copia di un dato condiviso e lo aggiunge ai propri
	// se vengono rispettati i controlli di identità
	public void copy(String Owner, String passw, E data) throws NullPointerException, IncorrectCredentialsException, DataNotFoundException;
	/*REQUIRES: owner!=null, passw!=null, data!=null
	  THROWS: se owner=null o passw = null o data=null throws NullPointerException
			  se non sono rispettati i controlli d'identità throw IncorrectCredentialsException	
			  se data non è presente tra gli oggetti condivisi con Owner throw ItemDoesntExist
	  MODIFIES: this
	  EFFECTS: Cerca data negli oggetti condivisi con Owner
			   e lo inserisce tra gli oggetti di cui è proprietario
	 */
	
	// Condivide il dato nella collezione con un altro utente
	// se vengono rispettati i controlli di identità
	public void share(String Owner, String passw, String Other, E data)throws NullPointerException, IllegalArgumentException, UserNotFoundException, IncorrectCredentialsException, DataNotFoundException;
	/*REQUIRES: owner!=null, passw!=null, data!=null
	  THROWS: se owner=null o passw = null o data=null throws NullPointerException
			  se Owner è uguale a Other throws IllegalArgumentException
			  se Other non esiste in this throw UserNotFoundException
			  se non sono rispettati i controlli d'identità throw IncorrectCredentialsException	
			  se data non è presente tra gli oggetti condivisi con Owner throw ItemDoesntExist
	  		  se Owner ha già condiviso data con Other allora throw DuplicateException
	  MODIFIES: this
	  EFFECTS: aggiunge data nella collezione di oggetti condivisi di Other 
    */
	
	//Rimuove la condivisione del dato da parte di un utente con un altro
	//se vengono rispettati i controlli di identità
	public void removeShare(String Owner, String passw, String Other, E data) throws NullPointerException, IllegalArgumentException, UserNotFoundException;
	/*REQUIRES: owner!=null, passw!=null, data!=null
	  THROWS: se owner=null o passw = null o data=null throws NullPointerException
			  se Owner è uguale a Other throws IllegalArgumentException
			  se Other non esiste in this throw UserNotFoundException
			  se non sono rispettati i controlli d'identità throw IncorrectCredentialsException	
			  se data non è presente tra gli oggetti condivisi con Owner throw ItemDoesntExist
	  MODIFIES: this
	  EFFECTS: rimuove data nella collezione di oggetti condivisi di Other 
  */
	
	// restituisce un iteratore (senza remove) che genera tutti i dati
	//dell’utente in ordine arbitrario
	// se vengono rispettati i controlli di identità
	public Iterator<E> getIterator(String Owner, String passw);
	/*REQUIRES: owner!=null, passw!=null, data!=null
	  THROWS: se owner=null o passw = null o data=null throws NullPointerException
			  se non sono rispettati i controlli d'identità throw IncorrectCredentialsException
	  EFFECTS: Restituisce un iteratore degli oggetti di tipo E posseduti da Owner
    */
	
	
}
