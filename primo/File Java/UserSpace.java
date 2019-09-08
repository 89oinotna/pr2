package progettopr2;


import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class UserSpace<E> {
	
   /* Overview: Collezione modificabile che rappresenta gli oggetti di cui è proprietario un utente
	* 				e gli oggetti che sono stati condivisi con lui 
    * Typical Element: < {owned_0, .., owned_n-1}, {shared_obj_0, ..., shared_obj_m-1}> 
    *						dove owned_i è una coppia <obj, {<shared_id_0, num_shared_0>, .., <shared_id_k-1, num_shared_k-1>}> che rappresenta il
    *						    	     il proprio oggetto obj e la lista degli utenti con cui è stato condiviso
    *						     shared_obj_i è un oggetto di tipo E che rappresenta un elemento
    *						     	          condiviso con l'user,
    *							 num_shared_i è un Integer che rappresenta il numero di volte che data è stato condiviso da utenti diversi
    * 							 owned_i!=owned_j per ogni i!=j tc 0<=i<j<n,
    * 							 shared_id_i!=shared_id_j per ogni i!=j tc 0<=i<j<k,
    * 							 num_shared_i>0 per ogni i tc 0<=i<j<k
    * 						   e shared_obj_i!=shared_obj_j per ogni i!=j tc 0<=i<j<m
    *					
	* Rep Invariant
    *   RI(c) =  	owned_i!=null
    *  				&& shared_obj_i!=null
    *  				&& num_shared!=null
    *  				&& for all i t.c. 0 <= i < c.owned.lenght() => c.owned.values().toArray()[i].obj()!=null
    *   			&& for all i,j t.c. 0 <= i < j < c.owned.lenght() => 																					//Dati posseduti distini
    *   					c.owned.values().toArray()[i].obj() != c.owned.values().toArray()[j].obj()
    *         		&& for all i t.c. 0 <= i < c.owned.lenght() => 																							//Id con i quali è condiviso un dato distinti
    *         				for all j,k t.c 0 <= j < k < c.owned.values().toArray()[i].shared_id.lenght() =>	
    *          					c.owned.values().toArray()[i].shared_id[j] != c.owned.values().toArray()[i].shared_id[k]
    * 				&& for all i,j t.c. 0 <= i < j < c.shared_obj.lenght() => c.shared_obj.values().toArray()[i] != c.shared_obj.values().toArray()[j]    	//Dati condivisi distinti
    * 				&& for all i t.c. 0 <= i < c.shared_obj.lenght() => c.shared_obj.values().toArray()[i] != null
    * 				&& for all i t.c. 0 <= i < c.shared_obj.lenght() => c.num_obj.values().toArray()[i] > 0
    *         		
    * Abstraction Function
    * AF(c) = <{ c.owned.values().toArray()[i] | 0 <= i < c.owned.values().toArray().lenght()}, {  c.shared_obj.values().toArray()[i] | 0 <= i < c.shared_obj.values().toArray().lenght() }>
	*/
    private Hashtable<E, UserData<E>> owned;
	private Hashtable<E, E> shared_obj;
	private Hashtable<E, Integer> num_shared;
	
	
	public UserSpace(){
		
		owned=new Hashtable<E,UserData<E>>();
		shared_obj=new Hashtable<E,E>();
		num_shared=new Hashtable<E,Integer>();
		
	}
	
	
	
	//restituisce il numero degli elementi posseduti
	public int getSize() {
		/*
		 * EFFECTS: restituisce il numero degli elementi owned contenuti nella collezione
		 */
		return owned.size();
	}

	//aggiunge data alla collezione di oggetti posseduti
	public void addObj(E data) throws DuplicateException{
		/* MODIFIES: this
		 * THROWS: se data è presente nella collezione di oggetti posseduti throw DuplicateException
		 * EFFECTS: inserisco la coppia <data, {}> nella collezione di elementi posseduti
		 */
		UserData<E> tmp= new UserData<E>(data);
		if(owned.putIfAbsent(data, tmp)!=null)
			throw new DuplicateException("Already owned");
		
	}

	//restituisce l'oggetto della collezione di user che è uguale a data
	public E getObj(E data) throws DataNotFoundException{
		
		/* THROWS: se data non è presente nella collezione di oggetti posseduti  throw DataNotFoundException
		 * EFFECTS: restituisce l'obj di owned.get(data);
		 */
		
		if(owned.containsKey(data))
			return owned.get(data).getObj();
		throw new DataNotFoundException("Data Not Found");
	}
	
	
	//restituisce la lista degli User con cui era condiviso l'oggetto rimosso
	public Hashtable<String, String> removeObj(E data) throws DataNotFoundException{
		/* MODIFIES: this
		 * THROWS: se data non è presente nella collezione di oggetti posseduti throw DataNotFoundException
		 * EFFECTS: rimuove data negli oggetti posseduti e restituisce un oggetto Hashtable<String, String> 
		 * 			che usa come chiavi e come dati gli id degli utenti con cui è condiviso data
		 */
		UserData<E> tmp=owned.get(data);
		if (tmp!=null) {
				Hashtable<String, String> shared_id=tmp.getSharedList();
				owned.remove(data); 
				return shared_id;
		}
		
		throw new DataNotFoundException("Data Not Found");
		 
		
	}
	
	
	//Aggiunge l'utente alla collezione di utenti con cui è condiviso un oggetto di cui si è proprietari
	public void addShare(String id, E data) throws DataNotFoundException, DuplicateException{
		/* MODIFIES: this
		 * THROWS: se data non è presente nella collezione di oggetti posseduti throw DataNotFoundException
		 *         se id è presente nella collezione di user con i quali è condiviso data throw DuplicateException
		 * EFFECTS: aggiunge id nella collezione di user con i quali è condiviso data		 
		 */
		if (!owned.containsKey(data)) {
			throw new DataNotFoundException("Data Not Found");
		}
		if(owned.get(data).getSharedList().contains(id)){
			throw new DuplicateException("Already shared with: "+id);
		}
		owned.get(data).addShare(id);
	}
	
	//Rimuove l'id dalla collezione di utenti con cui è condiviso un oggetto di cui si è proprietari
	public void removeShare(String id, E data) throws DataNotFoundException{
		/* MODIFIES: this
		 * THROWS: se data non è presente nella collezione di oggetti posseduti throw DataNotFoundException
		 * EFFECTS: Rimuove id è presente nella collezione di user con i quali è condiviso data
		 * 			se id non è presente non fa nulla
		 */
		if (!owned.containsKey(data)) {
			throw new DataNotFoundException("Data Not Found");
		}
		owned.get(data).removeShare(id);
	}

	//Aggiunge l'oggetto alla collezione di oggetti condivisi con l'user
	public void addShared(E data) {
		/* MODIFIES: this
		 * EFFECTS: se data non è presente nella collezione di oggetti condivisi
		 * 				 allora lo inserisce data con contatore a 1
		 *         	altrimenti incrementa di uno il contatore
		 */
		if(shared_obj.putIfAbsent(data, data)!=null) {
			
			Integer i=num_shared.get(data);
			i++;
			num_shared.put(data, i);
		}
		else{
			num_shared.put(data, 1);
		}
	}

	//Rimuove l'oggetto dalla collezione di oggetti condivisi con l'user
	public void removeFromShared(E data) {
		/* MODIFIES: this
		 * EFFECTS: se il numero di volte che data è stato condiviso è >1 
		 * 				decrementa di 1 il contatore
		 * 			altrimenti rimuove data da shared_obj
		 */
		Integer i=num_shared.get(data);
		if(i==1){
			shared_obj.remove(data);
			num_shared.remove(data);
		}
		else{
			i--;
			num_shared.put(data, i);
		}
	}

	// Ottiene una copia del valore del dato nella collezione di oggetti condivisi con user
	public E getSharedObj(E data) throws DataNotFoundException {
		/* THOWS: se data non è presente nella collezione di oggetti condivisi con user throw DataNotFoundException
		 * EFFECTS: se data è presente nella collezione di oggetti condivisi con user 
		 *			 restituisce l'oggetto	 
		 */
		if (shared_obj.contains(data)) return shared_obj.get(data); 
		throw new DataNotFoundException("Data Not Found");
	}


	//Restituisce un iteratore sugli oggetti posseduti
	public Iterator<E> getItOwned(){
		Set<E> keys = owned.keySet(); 
		//Ottengo iteratore sul set 
	    Iterator<E> itr = keys.iterator();
	    return itr;
	}
	
	//Restituisce un iteratore sugli id degli utenti con 
	//cui è condiviso un oggetto di cui si è proprietari
	public Iterator<String> getItShared(E data){
		/*
		 * EFFECTS: restituisce un iteratore di stringhe che rappresentano
		 * 			gli id con cui è condiviso obj 
		 */
			Set<String> keys = owned.get(data).getSharedList().keySet(); 
		    
		    Iterator<String> itr = keys.iterator();
		    return itr;
	}
	
	
	
	
}


