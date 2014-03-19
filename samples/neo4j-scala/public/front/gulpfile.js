var gulp = require('gulp'),
    jshint = require('gulp-jshint'),
    uglify = require('gulp-uglify'),
    minifyCSS = require('gulp-minify-css'),
    exec = require('gulp-exec'),
    bower = require('gulp-bower'),
    livereload = require('gulp-livereload'),
    watch = require('gulp-watch'),
    concat = require('gulp-concat'),
    less = require('gulp-less');


/**
 * Default task
 */
gulp.task("default", ["build"]);


/**
 * Update : Run bower update cmd.
 */
gulp.task("update", function(){
    bower()
        .pipe(gulp.dest('./app/lib/'));
});

/**
 * Compile : JS Hint task
 */
gulp.task('jshint', function() {
    gulp.src('./app/js/*.js')
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
});

/**
 * Compile : LESS compilation
 */
gulp.task('less', function() {
    gulp.src('./app/less/**/*.less')
        .pipe(less())
        .pipe(minifyCSS())
        .pipe(gulp.dest('./app/css'))
});

/**
 * Compile task.
 */
gulp.task('compile', ['jshint', 'less']);

/**
 * Build task  : update & compile
 */
gulp.task("build", ['update', 'compile']);

/**
 * Gulp watch : on each change file, run compile task
 */
gulp.task('watch', function() {
    var server = livereload();
    // watch change files into dest files
    gulp.watch('./app/**').on('change', function(file) {
        server.changed(file.path);
        gulp.run('compile');
    });
});