function Courselist(){
const courses = null;
    const complist = courses.map((course)=><Griffen key = {course.id} name = {course.name} age={course.age}/>)
    return (
        <>
        {complist}
        </>
    )
}

export default Courselist