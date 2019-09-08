package progettopr2;

import java.util.Hashtable;
import java.util.Iterator;




public class MySecureDataContainerHT <E> implements SecureDataContainer<E> {
	 /* Overview: Collezione modificabile per la memorizzazione e condivisione di dati di tipo E 
		*			tramite autenticazione con id e password
		*
	    * Rep Invariant
		*   RI(c) =    c.user_id != null
		*           && c.user_pw != null
		*			&& c.user_space!=null
		*			&& c.user_id.values().toArray().lenght()=c.user_pw.values().toArray().lenght()=c.user_space.values().toArray().lenght()
		*			&& for all i,j t.c. 0 <= i < j < c.user_id.values().toArray().lenght() =>     					//Utenti distinti
		*    					c.user_id.values().toArray()[i]!=c.user_id.values().toArray()[j]
		*			&& for all i t.c. 0 <= i < c.user_space.values().toArray().lenght() => 							//Dati posseduti non nulli
		*					for all j t.c. 0 <= i < c.user_space.values().toArray()[i].owned.lenght() =>
		*						c.user_space.values().toArray()[i].owned[j].obj()!=null
		*         	&& for all i t.c. 0 <= i < c.user_space.values().toArray().lenght() => 							//Dati posseduti non duplicati
		*         			for all j,k t.c. 0 <= j < k < c.user_space.values().toArray()[i].owned.lenght() => 
		*         					c.user_space.values().toArray()[i].owned[j].obj!=c.user_space.values().toArray()[i].owned[k].obj
		*         	&& for all i t.c. 0 <= i < c.user_space.values().toArray().lenght() => 							//Dati condivisi non nulli	
		*         			for all j t.c. 0 <= j < c.user_space.values().toArray()[i].shared_obj.lenght() => 
		*         				c.user_space.values().toArray()[i].shared_obj[j] != null
		*       	&& for all i t.c. 0 <= i < c.user_space.values().toArray().lenght() => 							//Id con i quali è condiviso un dato distinti
		*         			for all j t.c 0 <= j < c.user_space.values().toArray()[i].owned[j].lenght() =>	
		*          					for all k,t t.c 0 <= k < t < c.user_space.values().toArray()[i].owned.lenght() =>
		*         							c.user_space.values().toArray()[i].owned[j].shared_id[j] != c.user_space.values().toArray()[i].owned[j].shared_id[k]
		*         	&& for all i t.c. 0 <= i < c.user_space.values().toArray().lenght() => 							//Id con i quali è condiviso un dato esistono
		*         			for all j t.c 0 <= j < c.user_space.values().toArray()[i].owned.lenght() =>
		*         					for all k t.c 0 <= k < c.user_space.values().toArray()[i].owned[j].shared_id.lenght() =>	
		*          							Exists t con 0 <= t < c.user_id.values().toArray().lenght() t.c.					
		*          									c.user_id.values().toArray()[t]==c.user_space.values().toArray()[i].owned[j].shared_id[k]
		*         	&& for all i t.c. 0 <= i < c.user_space.values().toArray().lenght() => 							//Dati condivisi non duplicati
		*         			for all j,k t.c. 0 <= j < k < c.user_space.values().toArray()[i].shared_obj.lenght() => 
		*         					c.user_space.values().toArray()[i].shared_obj[j]!=c.user_space.values().toArray()[i].shared_obj[k]
		*         	&& for all i t.c. 0 <= i < c.user_space.values().toArray().lenght() => 												//Esiste un proprietario dei dati condivisi
		*         			for all j t.c. 0 <= j < c.user_space.values().toArray()[i].shared_obj.lenght() =>
		*         					Exists t con 0 <= t < c.user_space.values().toArray().lenght() t.c. 
		*         							Exists z con 0 <= z < c.user_space.values().toArray()[t].owned.lenght() t.c. 
		*         									c.user_space.values().toArray()[t].owned[z].obj==c.user_space.values().toArray()[i].shared_obj[j]       
		*   
		* Abstraction Function
		* AF(c) = <id, pw, user_space>
		*			dove:
		*				id=c.user_id.values().toArray()[i] | 0<=i<c.user_id.values().toArray().lenght()
		*				pw=c.user_pw.values().toArray()[i] |  0<=i<c.user_pw.values().toArray().lenght()
		*				user_space=c.user_space.values().toArray()[i] | 0<=i<c.user_space.values().toArray().lenght()
		*/
		private Hashtable<String,String> user_id;
		private Hashtable<String,String> user_pw;
		private Hashtable<String, UserSpace<E>> user_space;
		
		public MySecureDataContainerHT(){
			/* EFFECTS: Inizializza this < {}, {}, {} >
			*/
			user_id=new Hashtable<String,String>();
			user_pw=new Hashtable<String,String>();
			user_space=new Hashtable<String,UserSpace<E>>();
		}
		
		public MySecureDataContainerHT(String Id, String passw) throws NullPointerException{
			/* REQUIRE: id!=null, passw!=null
			 * THROWS: se id=null o passw = null throws NullPointerException
			 * EFFECTS: Inizializza this <id, passw, user_space>
		 				dove user space è <{}, {}>
			 */
			if(Id==null||passw==null) throw new NullPointerException();
			user_id=new Hashtable<String,String>();
			user_pw=new Hashtable<String,String>();
			user_space=new Hashtable<String,UserSpace<E>>();
			user_id.putIfAbsent(Id, Id);
			user_pw.putIfAbsent(Id, passw);
			UserSpace<E> newUser=new UserSpace<E>();
			user_space.putIfAbsent(Id, newUser);
		}
		
		// Crea l’identità un nuovo utente della collezione
		public void createUser(String Id, String passw) throws NullPointerException, DuplicateException{
			/*	REQUIRES: id!=null, passw!=null
	  		THROWS: se id=null o passw = null throws NullPointerException
	  				se esiste già la coppia <Id, passw> in this throws DuplicateException
	 		MODIFIES: user_id, user_pw, user_space
	 		EFFECTS: aggiunge a this <id, passw, user_space>
	 				dove user space è <{}, {}>
			 */
			if(Id==null||passw==null) throw new NullPointerException();
			if(user_id.putIfAbsent(Id, Id)==null){
				user_pw.putIfAbsent(Id, passw);
				UserSpace<E> newUser=new UserSpace<E>();
				user_space.putIfAbsent(Id, newUser);
				
			}
			else{
				throw new DuplicateException("This user already exists");
			}
			
		}

		// Restituisce il numero degli elementi di un utente presenti nella
		// collezione
		public int getSize(String Owner, String passw) throws NullPointerException,IncorrectCredentialsException{
			/*	REQUIRES: owner!=null passw!=null
	  		THROWS:	se id=null o passw=null throw NullPointerException
	  	            se non esiste la coppia <Owner, passw> in this throw IncorrectCredentialsException
			EFFECTS:Restituisce il numero di oggetti di cui è proprietario owner
			 */
			if(Owner==null||passw==null) throw new NullPointerException();
			auth(Owner, passw);
			return user_space.get(Owner).getSize();
			
		}

		// Inserisce il valore del dato nella collezione
		// se vengono rispettati i controlli di identità
		public boolean put(String Owner, String passw, E data) throws NullPointerException, IncorrectCredentialsException, DuplicateException {
			/*	REQUIRES: owner!=null, passw!=null, data!=null
  				THROWS: se owner=null o passw = null o data=null throws NullPointerException
  		 				se non esiste la coppia <Owner, passw> in this throw IncorrectCredentialsException
  		        		se esiste data negli oggetti di cui è proprietario Owner throw DuplicateException
 				MODIFIES: user_space
 				EFFECTS: Aggiunge data agli elementi di cui è proprietario owner e restituisce true
			 */
			if(Owner==null||passw==null||data==null) throw new NullPointerException();
			auth(Owner, passw);
			user_space.get(Owner).addObj(data);
			return true;
			
		}

		// Ottiene una copia del valore del dato nella collezione
		// se vengono rispettati i controlli di identità
		public E get(String Owner, String passw, E data)  throws NullPointerException, DataNotFoundException, IncorrectCredentialsException{
			/*	REQUIRES: owner!=null, passw!=null, data!=null
				THROWS: se owner=null o passw = null o data=null throws NullPointerException
		 				se non esiste la coppia <Owner, passw> throw IncorrectCredentialsException	
						se data non è presente tra gli oggetti di cui Owner è proprietario e quelli condivisi
				   		con lui throw DataNotFoundException
				EFFECTS: Restituisce una shallow copy dell'occorrenza di data nella collezione di Owner
						cercando prima tra gli oggetti di cui è proprietario e poi tra i condivisi
			 */
			if(Owner==null||passw==null||data==null) throw new NullPointerException();
			auth(Owner, passw);
			try {
				
				return user_space.get(Owner).getObj(data); 
			}
			catch (DataNotFoundException e) {
				
				return user_space.get(Owner).getSharedObj(data);
				
			}
			
		}

		// Rimuove il dato nella collezione
		// se vengono rispettati i controlli di identità
		public E remove(String Owner, String passw, E data) throws NullPointerException, DataNotFoundException, IncorrectCredentialsException{
			/*	REQUIRES: owner!=null, passw!=null, data!=null
				THROWS: se owner=null o passw = null o data=null throws NullPointerException
						se non esiste la coppia <Owner, passw> throw IncorrectCredentialsException	
						se data non è presente tra gli oggetti di cui è proprietario Owner throw DataNotFoundException
				MODIFIES: user_space
				EFFECTS: Rimuove l'occorrenza di data dagli oggetti di cui è proprietario owner
			 		 	e restituisce l'elemento rimosso
		    */
			if(Owner==null||passw==null||data==null) throw new NullPointerException();
			auth(Owner, passw);
			E removed=user_space.get(Owner).getObj(data);
			Iterator<String> iterator=user_space.get(Owner).getItShared(data);
			while(iterator.hasNext()){//rimuove la condivisione dell'oggetto con tutti gli utenti
					user_space.get(Owner).removeFromShared(data);
			}
			user_space.get(data).removeObj(data);
			return removed;
		}

		// Crea una copia di un dato condiviso e lo aggiunge ai propri
		// se vengono rispettati i controlli di identità
		public void copy(String Owner, String passw, E data)  throws NullPointerException, IncorrectCredentialsException, DataNotFoundException{
			/*REQUIRES: owner!=null, passw!=null, data!=null
			  THROWS: se owner=null o passw = null o data=null throws NullPointerException
					  se non non esiste la coppia <Owner, passw> throw IncorrectCredentialsException	
					  se data non è presente tra gli oggetti condivisi con Owner throw DataNotFoundException
			  MODIFIES: user_space
			  EFFECTS: Cerca l'occorrenza di data negli oggetti condivisi con Owner
					   e inserisce un riferimento ad essa tra gli oggetti di cui è proprietario
			 */
			if(Owner==null||passw==null||data==null) throw new NullPointerException();
			auth(Owner, passw);
			E newData=user_space.get(Owner).getSharedObj(data);
			
			this.put(Owner, passw, newData);
			
		}

		// Condivide il dato nella collezione con un altro utente
		// se vengono rispettati i controlli di identità
		public void share(String Owner, String passw, String Other, E data)  throws NullPointerException, DuplicateException, DataNotFoundException, IncorrectCredentialsException, IllegalArgumentException, UserNotFoundException{
			/*REQUIRES: owner!=null, passw!=null, data!=null
			  THROWS: se owner=null o passw = null o data=null throws NullPointerException
					  se Owner.equals(Other) throws IllegalArgumentException
					  se Other non esiste in this throw UserNotFoundException
					  se non esiste la coppia <Owner, passw> throw IncorrectCredentialsException	
					  se non esiste data tra gli oggetti condivisi con Owner throw DataNotFoundException
			  		  se Owner ha già condiviso data con Other allora throw DuplicateException
			  MODIFIES: user_space
			  EFFECTS: aggiunge data nella collezione di oggetti condivisi di Other 
		    */
			if(Owner==null||passw==null||data==null) throw new NullPointerException();
			if(Owner.equals(Other)) throw new IllegalArgumentException();
			auth(Owner, passw);
			if(user_id.containsKey(Other)) throw new UserNotFoundException("This user doesn't exists: "+Other);
			E obj=user_space.get(Owner).getObj(data);
			user_space.get(Owner).addShare(Other, obj);
			user_space.get(Other).addShared(obj);
			
			
		}
		
		//Rimuove la condivisione del dato con un utente
		public void removeShare(String Owner, String passw, String Other, E data) throws NullPointerException, DataNotFoundException, IllegalArgumentException, UserNotFoundException, IncorrectCredentialsException{
			/*REQUIRES: owner!=null, passw!=null, data!=null
			  THROWS: se owner=null o passw = null o data=null throws NullPointerException
					  se Owner è uguale a Other throws IllegalArgumentException
					  se Other non esiste in this throw UserNotFoundException
					  se non sono rispettati i controlli d'identità throw IncorrectCredentialsException	
					  se data non è presente tra gli oggetti condivisi con Owner throw DataNotFoundException
			  MODIFIES: user_space
			  EFFECTS: rimuove data nella collezione di oggetti condivisi di Other 
		  */
			if(Owner==null||passw==null||data==null) throw new NullPointerException();
			if(Owner.equals(Other)) throw new IllegalArgumentException();
			auth(Owner, passw);
			if(user_id.containsKey(Other)) throw new UserNotFoundException("This user doesn't exists: "+Other);
			user_space.get(Owner).removeShare(Other, data);
			user_space.get(Other).removeFromShared(data);
			
			
		}

		// restituisce un iteratore (senza remove) che genera tutti i dati
		//dell’utente in ordine arbitrario
		// se vengono rispettati i controlli di identità
		public Iterator<E> getIterator(String Owner, String passw)throws NullPointerException{
			/*REQUIRES: owner!=null, passw!=null, data!=null
			  THROWS: se owner=null o passw = null o data=null throws NullPointerException
					  se non esiste la coppia <Owner, passw> throw IncorrectCredentialsException
			  EFFECTS: se esiste la coppia <Owner, passw> restituisce un iteratore degli oggetti
			       	   di tipo E posseduti da Owner
			 */	
			auth(Owner, passw);
			return user_space.get(Owner).getItOwned();
		}

		//Verifica l'identità
		private void auth(String Id, String passw) throws IncorrectCredentialsException{
			/* THROWS: se non esiste la coppia <id, passw> throws IncorrectCredentialsException	
			 * EFFECTS: Verifica l'esistenza della coppia <id, passw>
			 */
			if(user_pw.get(Id)!=passw) 
			throw new IncorrectCredentialsException("Incorrect User or Password");
		}

	}

