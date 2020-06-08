# foodfactory

food factory implementation

the implementation has 3 main elements, assembly lines, ovens and storage units.
all of these elements are managed by Orchestrator threads.

the assembly line orchestrator thread starts 1...n assembly line threads with each one of them having a scheduled task to create new products over a period of time, in order to simulate the creation of new products on a real assembly line
after a period of time the head product in the line queue is marked as READY_TO_COOK, and sent to the oven thread to cook it.
If the oven orchestrator thread can't find a suitable oven, it is sent back to the originating assembly line, which then checks for storage space, if there's none available, the line is paused for some seconds
At the same time, the storage orchestrator thread checks for the used storage and attemps to cook the stored products.
The oven orchestrator thread checks if any products are already cooked in it's associated ovens, and if so, it's sent back to the originating line wich will redirect it to the associated end assembly line where coocked products are queued.

note: as I ran out of time i left obvervations along the code to explain how some things should be implemented in a real scenario

