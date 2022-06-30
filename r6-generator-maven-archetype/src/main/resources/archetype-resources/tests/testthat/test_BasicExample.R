
J = ${rPackageName}::JavaApi$get()


test_that("object oriented interface works", {
	ex = J$BasicExample$new("test message")
	res = ex$getMessage()
	testthat::expect_true(res == "test message")
})

# This is a test of the static method interface.
test_that("functional interface works", {
	res = ${rPackageName}::greet("tester")
	testthat::expect_true(res == "Hello, tester")
})