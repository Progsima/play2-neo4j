var gulp = require('gulp'),
    jshint = require('gulp-jshint'),
    uglify = require('gulp-uglify'),
    minifyCSS = require('gulp-minify-css'),
    exec = require('gulp-exec'),
    bower = require('gulp-bower'),
    livereload = require('gulp-livereload'),
    watch = require('gulp-watch'),
    concat = require('gulp-concat'),
    less = require('gulp-less'),
    clean = require('gulp-clean');

application = {
    less : {
        src : ['./app/less/**/*.less'],
        dest: "./app/build/css"
    },
    js : {
        src : [
            "./app/js/app.js",
            "./app/js/error.js",
            "./app/modules/lg-jsonschema-type/main.js",
            "./app/modules/lg-jsonschema-type/controllers.js",
            "./app/modules/lg-jsonschema-type/service.js",
            "./app/modules/lg-jsonschema-type/config.js",
            "./app/modules/lg-jsonschema-form/main.js",
            "./app/modules/lg-jsonschema-form/directive-form.js",
            "./app/modules/lg-jsonschema-object/main.js",
            "./app/modules/lg-jsonschema-object/controllers.js"
        ],
        dest : {
            folder : "./app/build/js",
            name : "main.js"
        }
    },
    lib : {
        src : [
            "./bower_components/angular/angular.js",
            "./bower_components/angular-route/angular-route.js",
            "./bower_components/lodash/dist/lodash.js",
            "./bower_components/restangular/dist/restangular.js",
            "./bower_components/ng-table/ng-table.js"
        ],
        dest : {
            folder : "./app/build/js",
            name : "lib.js"
        }
    }
}


/**
 * Default task
 */
gulp.task("default", ["watch"]);

/**
 * Build all project source.
 */
gulp.task("build", ["clean", "bower", "less", "js"]);

/**
 * Bower : update lib dependencies, concat & minify them.
 */
gulp.task("bower", function(){
    bower();
    gulp.src(application.lib.src)
        .pipe(concat(application.lib.dest.name))
        .pipe(uglify())
        .pipe(gulp.dest(application.lib.dest.folder));
});

/**
 * JS Hint task.
 */
gulp.task('jshint', function() {
    gulp.src(application.js.src)
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
});

/**
 * LESS compilation.
 */
gulp.task('less', function() {
    gulp.src(application.less.src)
        .pipe(less())
        .pipe(minifyCSS())
        .pipe(gulp.dest(application.less.dest));
});

/**
 * Concat & minify JS application files.
 */
gulp.task('js', function(){
    gulp.src(application.js.src)
        .pipe(concat(application.js.dest.name))
        //.pipe(uglify())
        .pipe(gulp.dest(application.js.dest.folder));
});

/**
 * Clean task
 */
gulp.task('clean', function() {
    gulp.src('app/build', {read: false}).pipe(clean());
    gulp.src('app/bowser_components', {read: false}).pipe(clean());
});

/**
 * Gulp watch : on each change file.
 */
gulp.task('watch', function() {

    gulp.run("bower");

    gulp.src(application.js.src, { read: false})
        .pipe(watch({ emit: 'all' }, function(files) {
            gulp.run("js");
            files
                .pipe(livereload())
                .pipe(jshint())
                .pipe(jshint.reporter('default'));

        }));

    gulp.src(application.less.src, { read: false})
        .pipe(watch({ emit: 'all' }, function(files) {
            gulp.run("less");
            files.pipe(livereload());
        }));
});