type ide = string;;
type 't env = (ide * 't) list;;
type exp = Eint of int | Ebool of bool | Den of ide | Prod of exp * exp | Sum of exp * exp | Diff of exp * exp |
	Eq of exp * exp | Minus of exp | IsZero of exp | Or of exp * exp | And of exp * exp | Not of exp |
	Ifthenelse of exp * exp * exp | Let of ide * exp * exp | Fun of ide * exp | FunCall of exp * exp |
	Letrec of ide * exp * exp 
	| ETree of tree (* gli alberi sono anche espressioni *)
	| ApplyOver of (ide list) * exp * exp (* applicazione di funzione ai nodi *)
	| Select of ide * exp (* selezione di un nodo *)
		and tree = Empty 
	| ENode of ide * exp * tree * tree;;

(*tipi esprimibili*)
type evT = Int of int | Bool of bool | Unbound | FunVal of ide * exp * evT env  | RecFunVal of ide * ide * exp * evT env 
| Tree of evTree and evTree = EmptyTree | Node of ide * evT * evTree * evTree ;;




(*ambiente*)

let emptyEnv = [ ("", Unbound) ] ;;
let bind (s:evT env) (i:ide) (x:evT) = ( i, x ) :: s;;
let rec lookup (s: evT env) (i:ide) = match s with
| [(_, e)] -> e
| [] -> Unbound
| (j,v)::sl when j = i -> v
| _::sl -> lookup sl i;;




(*rts*)
(*type checking*)
let typecheck (s : string) (v : evT) : bool = match s with
	"int" -> (match v with
		Int(_) -> true |
		_ -> false) |
	"bool" -> (match v with
		Bool(_) -> true |
		_ -> false) |
	_ -> failwith("not a valid type");;

(*ricerca nodo*)
let rec lookupNode (x:ide) (lst:ide list) :bool = match lst with 
													|[]->false
													|y::ys->if x=y then true
																	else lookupNode x ys;;


(*funzioni primitive*)
let prod x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n*u))
	else failwith("Type error");;

let sum x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n+u))
	else failwith("Type error");;

let diff x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n-u))
	else failwith("Type error");;

let eq x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Bool(n=u))
	else failwith("Type error");;

let minus x = if (typecheck "int" x) 
	then (match x with
	   	Int(n) -> Int(-n))
	else failwith("Type error");;

let iszero x = if (typecheck "int" x)
	then (match x with
		Int(n) -> Bool(n=0))
	else failwith("Type error");;

let vel x y = if (typecheck "bool" x) && (typecheck "bool" y)
	then (match (x,y) with
		(Bool(b),Bool(e)) -> (Bool(b||e)))
	else failwith("Type error");;

let et x y = if (typecheck "bool" x) && (typecheck "bool" y)
	then (match (x,y) with
		(Bool(b),Bool(e)) -> Bool(b&&e))
	else failwith("Type error");;

let non x = if (typecheck "bool" x)
	then (match x with
		Bool(true) -> Bool(false) |
		Bool(false) -> Bool(true))
	else failwith("Type error");;

(*interprete*)
let rec eval (e : exp) (r : evT env) : evT = match e with
 
	Eint n -> Int n |
	Ebool b -> Bool b |
	IsZero a -> iszero (eval a r) |
	Den i -> lookup r i |
	Eq(a, b) -> eq (eval a r) (eval b r) |
	Prod(a, b) -> prod (eval a r) (eval b r) |
	Sum(a, b) -> sum (eval a r) (eval b r) |
	Diff(a, b) -> diff (eval a r) (eval b r) |
	Minus a -> minus (eval a r) |
	And(a, b) -> et (eval a r) (eval b r) |
	Or(a, b) -> vel (eval a r) (eval b r) |
	Not a -> non (eval a r) |
	Ifthenelse(a, b, c) -> 
		let g = (eval a r) in
			if (typecheck "bool" g) 
				then (if g = Bool(true) then (eval b r) else (eval c r))
				else failwith ("nonboolean guard") |
	Let(i, e1, e2) -> eval e2 (bind r i (eval e1 r)) |
	Fun(i, a) -> FunVal(i, a, r) |
	FunCall(f, eArg) -> 
		let fClosure = (eval f r) in
			(match fClosure with
				FunVal(arg, fBody, fDecEnv) -> 
					eval fBody (bind fDecEnv arg (eval eArg r)) 
				|RecFunVal(g, arg, fBody, fDecEnv) -> 
					let aVal = (eval eArg r) in
						let rEnv = (bind fDecEnv g fClosure) in
							let aEnv = (bind rEnv arg aVal) in
								eval fBody aEnv 
				|_ -> failwith("non functional value")) 
	|Letrec(f, funDef, lBody) ->(match funDef with
            		Fun(i, fBody) -> let r1 = (bind r f (RecFunVal(f, i, fBody, r))) in
                         			                eval lBody r1 
				|_ -> failwith("non functional def"))
	
	| ApplyOver(idl, exf, ext) -> (match (eval exf r), (eval ext r) with
								|FunVal(arg, fbody, fDecEnv),Tree(a) -> let rec applyFun (t:evTree) : evTree  =
										match t with
										|EmptyTree -> EmptyTree
										|Node(id, ex, lt, rt)-> if lookupNode id idl 
																	then Node(id, (eval fbody (bind fDecEnv arg ex)), applyFun(lt), applyFun(rt))
																  else Node(id, ex, applyFun(lt), applyFun(rt))
									in Tree( applyFun (a) )
								|(_, _) -> failwith("type error"))
	
	| Select(tag, a) -> (match (eval a r) with
						|Tree(t)-> let rec aux (al:evTree) : evTree= 
										match al with
										|EmptyTree->EmptyTree
										|Node(id,ex,lt,rt) -> 	if id=tag then Node(id,ex,lt,rt)
																else 
																	let left = aux (lt) in
																	let right = aux (rt) in
																	match left, right with
																	|(EmptyTree, EmptyTree) ->EmptyTree
																	|(Node(_),EmptyTree)->left
																	|(EmptyTree,Node(_))->right
																	|(Node(_),Node(_))->failwith("tag duplicati")
									in Tree( aux (t) )
						|_->failwith("type error")	)					
	|ETree(a) -> let rec evalTree(t : tree) : evTree =
				match t with
				|Empty -> EmptyTree
				|ENode(id, ex, lt, rt) -> Node(id, (eval ex r), evalTree(lt), evalTree(rt))
			in Tree( evalTree(a) )						
	;;
		
(* =================  TESTS  ================= *)


let bt = (*albero*)
ETree(ENode("a", Eint 1,      
ENode("b", Eint 2, 
ENode("d", Eint 4, ENode("h", Eint 8, Empty, Empty), 
ENode("i", Eint 9, ENode("l", Eint 12, Empty, Empty), Empty)), 
ENode("e", Eint 5, Empty, Empty)), 
ENode("c", Eint 3, ENode("f", Eint 6, Empty, Empty), 
ENode("g", Eint 7, 
ENode("j", Eint 10, Empty, 
ENode("m", Eint 13, Empty, Empty)), 
ENode("k", Eint 11, Empty, Empty)))));;


(*test ETree*)
eval bt emptyEnv;;

(*test ApplyOver *)
eval (Let("f", Fun("x", Sum(Den "x", Eint 1)),  (*dichiarazione di funzione (aggiunge 1 ai nodi)*)
Let("t", bt, ApplyOver(["a";"b";"c";"d"], Den "f", Den "t"))))
emptyEnv;;

(*test Select (nodo non trovato)*)
eval (Select("n",bt)) emptyEnv;;

(*test Select (nodo trovato)*)
eval (Select("a",bt)) emptyEnv;;