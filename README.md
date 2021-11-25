# Dining Philosophers  
A Java solution to the Dining Philosophers problem developed as a university assignment for the subject of Operating Systems. On startup, the user is prompted to enter the number of philosophers in the simulation. Then, the philosophers are initialized with adjacent forks between each other as they "sit" on the lunch table. Each philosopher can be in one of three states: thinking, hungry or eating. As they get hungry and eventually start eating, they lift their forks, thus locking them for the amount of time required to eat. When forks are locked, adjacent philosophers must wait for the neighbor to release them. All state changes are displayed in console as well as the average waiting time for each philosopher.

**Browse through related projects on thanosparavantis.com:**  
https://www.thanosparavantis.com/projects/dining-philosophers
