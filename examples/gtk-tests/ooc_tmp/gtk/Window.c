/*
 * Generated by ooc, the Object-Oriented C compiler, by Amos Wenger, 2009
 */

// OOC dependencies
#include "Window.h"


/**
 * A simple Gtk window
 */

/*
 * Definition of class gtk.Window
 */

gtk_Window__class gtk_Window__classInstance;


GtkObject * __gtk_Window_getObject(struct gtk_Window*  this) {
	
	return GTK_OBJECT(this->window);


}

/**
 * Change the title of this window
 */
Void __gtk_Window_setTitle_String(struct gtk_Window*  this, String title) {
	
	gtk_window_set_title(GTK_WINDOW(this->window), title);


}

/**
 * Create a new top-level window
 */
struct gtk_Window*  __gtk_Window_new() {

	gtk_Window this = GC_malloc(sizeof(struct gtk_Window));

	if(gtk_Window__classInstance == NULL) {
		gtk_Window__classInstance = GC_malloc(sizeof(struct gtk_Window__class));
		gtk_Window__classInstance ->name = "gtk.Window";
	}
	this->class = gtk_Window__classInstance;

	this->class->__getObject = (GtkObject * (*)(struct gtk_Window* )) &__gtk_Window_getObject;
	this->class->__connect_String_Func = (Void (*)(struct gtk_Window* , String, Func)) &__gtk_GObject_connect_String_Func;
	this->class->__connect_String_Func_GPointer = (Void (*)(struct gtk_Window* , String, Func, GPointer)) &__gtk_GObject_connect_String_Func_GPointer;
	this->class->__connectNaked_String_Func = (Void (*)(struct gtk_Window* , String, Func)) &__gtk_GObject_connectNaked_String_Func;
	this->class->__connectNaked_String_Func_GPointer = (Void (*)(struct gtk_Window* , String, Func, GPointer)) &__gtk_GObject_connectNaked_String_Func_GPointer;
	this->class->__emitByName_String = (Void (*)(struct gtk_Window* , String)) &__gtk_GObject_emitByName_String;
	this->class->__ref = (Void (*)(struct gtk_Window* )) &__gtk_GObject_ref;
	this->class->__unref = (Void (*)(struct gtk_Window* )) &__gtk_GObject_unref;
	this->class->__sink = (Void (*)(struct gtk_Window* )) &__gtk_GObject_sink;
	this->class->__setProperty_String_GValue__star = (Void (*)(struct gtk_Window* , String, GValue *)) &__gtk_GObject_setProperty_String_GValue__star;
	this->class->__getWidget = (GtkWidget * (*)(struct gtk_Window* )) &__gtk_Widget_getWidget;
	this->class->__setSensitive_Bool = (Void (*)(struct gtk_Window* , Bool)) &__gtk_Widget_setSensitive_Bool;
	this->class->__isRealized = (Bool (*)(struct gtk_Window* )) &__gtk_Widget_isRealized;
	this->class->__realize = (Void (*)(struct gtk_Window* )) &__gtk_Widget_realize;
	this->class->__forceRepaint_Bool = (Void (*)(struct gtk_Window* , Bool)) &__gtk_Widget_forceRepaint_Bool;
	this->class->__show = (Void (*)(struct gtk_Window* )) &__gtk_Widget_show;
	this->class->__showAll = (Void (*)(struct gtk_Window* )) &__gtk_Widget_showAll;
	this->class->__hide = (Void (*)(struct gtk_Window* )) &__gtk_Widget_hide;
	this->class->__destroy = (Void (*)(struct gtk_Window* )) &__gtk_Widget_destroy;
	this->class->__setPosition_gint_gint = (Void (*)(struct gtk_Window* , gint, gint)) &__gtk_Widget_setPosition_gint_gint;
	this->class->__setUSize_gint_gint = (Void (*)(struct gtk_Window* , gint, gint)) &__gtk_Widget_setUSize_gint_gint;
	this->class->__setEvents_Int = (Void (*)(struct gtk_Window* , Int)) &__gtk_Widget_setEvents_Int;
	this->class->__getAllocation = (GtkAllocation (*)(struct gtk_Window* )) &__gtk_Widget_getAllocation;
	this->class->__getWidth = (Int (*)(struct gtk_Window* )) &__gtk_Widget_getWidth;
	this->class->__getHeight = (Int (*)(struct gtk_Window* )) &__gtk_Widget_getHeight;
	this->class->__getStyle = (struct gtk_Style*  (*)(struct gtk_Window* )) &__gtk_Widget_getStyle;
	this->class->__add_Widget = (Void (*)(struct gtk_Window* , struct gtk_Widget* )) &__gtk_Container_add_Widget;
	this->class->__remove_Widget = (Void (*)(struct gtk_Window* , struct gtk_Widget* )) &__gtk_Container_remove_Widget;
	this->class->__setBorderWidth_Int = (Void (*)(struct gtk_Window* , Int)) &__gtk_Container_setBorderWidth_Int;
	this->class->__setTitle_String = (Void (*)(struct gtk_Window* , String)) &__gtk_Window_setTitle_String;
	
	this->window = GTK_WINDOW(gtk_window_new(GTK_WINDOW_TOPLEVEL));

	return this;


}

/**
 * Create a new titled top-level window
 */
struct gtk_Window*  __gtk_Window_new_String(String title) {

	gtk_Window this = GC_malloc(sizeof(struct gtk_Window));

	if(gtk_Window__classInstance == NULL) {
		gtk_Window__classInstance = GC_malloc(sizeof(struct gtk_Window__class));
		gtk_Window__classInstance ->name = "gtk.Window";
	}
	this->class = gtk_Window__classInstance;

	this->class->__getObject = (GtkObject * (*)(struct gtk_Window* )) &__gtk_Window_getObject;
	this->class->__connect_String_Func = (Void (*)(struct gtk_Window* , String, Func)) &__gtk_GObject_connect_String_Func;
	this->class->__connect_String_Func_GPointer = (Void (*)(struct gtk_Window* , String, Func, GPointer)) &__gtk_GObject_connect_String_Func_GPointer;
	this->class->__connectNaked_String_Func = (Void (*)(struct gtk_Window* , String, Func)) &__gtk_GObject_connectNaked_String_Func;
	this->class->__connectNaked_String_Func_GPointer = (Void (*)(struct gtk_Window* , String, Func, GPointer)) &__gtk_GObject_connectNaked_String_Func_GPointer;
	this->class->__emitByName_String = (Void (*)(struct gtk_Window* , String)) &__gtk_GObject_emitByName_String;
	this->class->__ref = (Void (*)(struct gtk_Window* )) &__gtk_GObject_ref;
	this->class->__unref = (Void (*)(struct gtk_Window* )) &__gtk_GObject_unref;
	this->class->__sink = (Void (*)(struct gtk_Window* )) &__gtk_GObject_sink;
	this->class->__setProperty_String_GValue__star = (Void (*)(struct gtk_Window* , String, GValue *)) &__gtk_GObject_setProperty_String_GValue__star;
	this->class->__getWidget = (GtkWidget * (*)(struct gtk_Window* )) &__gtk_Widget_getWidget;
	this->class->__setSensitive_Bool = (Void (*)(struct gtk_Window* , Bool)) &__gtk_Widget_setSensitive_Bool;
	this->class->__isRealized = (Bool (*)(struct gtk_Window* )) &__gtk_Widget_isRealized;
	this->class->__realize = (Void (*)(struct gtk_Window* )) &__gtk_Widget_realize;
	this->class->__forceRepaint_Bool = (Void (*)(struct gtk_Window* , Bool)) &__gtk_Widget_forceRepaint_Bool;
	this->class->__show = (Void (*)(struct gtk_Window* )) &__gtk_Widget_show;
	this->class->__showAll = (Void (*)(struct gtk_Window* )) &__gtk_Widget_showAll;
	this->class->__hide = (Void (*)(struct gtk_Window* )) &__gtk_Widget_hide;
	this->class->__destroy = (Void (*)(struct gtk_Window* )) &__gtk_Widget_destroy;
	this->class->__setPosition_gint_gint = (Void (*)(struct gtk_Window* , gint, gint)) &__gtk_Widget_setPosition_gint_gint;
	this->class->__setUSize_gint_gint = (Void (*)(struct gtk_Window* , gint, gint)) &__gtk_Widget_setUSize_gint_gint;
	this->class->__setEvents_Int = (Void (*)(struct gtk_Window* , Int)) &__gtk_Widget_setEvents_Int;
	this->class->__getAllocation = (GtkAllocation (*)(struct gtk_Window* )) &__gtk_Widget_getAllocation;
	this->class->__getWidth = (Int (*)(struct gtk_Window* )) &__gtk_Widget_getWidth;
	this->class->__getHeight = (Int (*)(struct gtk_Window* )) &__gtk_Widget_getHeight;
	this->class->__getStyle = (struct gtk_Style*  (*)(struct gtk_Window* )) &__gtk_Widget_getStyle;
	this->class->__add_Widget = (Void (*)(struct gtk_Window* , struct gtk_Widget* )) &__gtk_Container_add_Widget;
	this->class->__remove_Widget = (Void (*)(struct gtk_Window* , struct gtk_Widget* )) &__gtk_Container_remove_Widget;
	this->class->__setBorderWidth_Int = (Void (*)(struct gtk_Window* , Int)) &__gtk_Container_setBorderWidth_Int;
	this->class->__setTitle_String = (Void (*)(struct gtk_Window* , String)) &__gtk_Window_setTitle_String;
	
	this->window = GTK_WINDOW(gtk_window_new(GTK_WINDOW_TOPLEVEL));;
	this->class->__setTitle_String(this, title);

	return this;


}