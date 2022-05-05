```kotlin

enum class TreeElementState {
    Open,
    Closed,
    Hidden
}

class TreeElement<T:Reactive> : ReactiveData<TreeElement<T>> {
    val state by TreeElementState.reactive
    val data by ReactiveData<T>
    val children by ReactiveList<TreeElement<T>>
}

class Tree(
    val data: ReactiveList<TreeElement>,
    val renderers: ReactiveList<TreeRenderer>
) : ReactiveElement {
    
    override fun render() {
        
    }
    
}

@Reactive
fun tree(
    data: ReactiveList<TreeElement>,
    renderers: ReactiveList<TreeRenderer>
) {

}


fun Container() {
    Tree(emptyReactiveList(), emptyReactiveList())
}
```
