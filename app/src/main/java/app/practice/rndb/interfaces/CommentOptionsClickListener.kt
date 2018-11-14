package app.practice.rndb.interfaces

import app.practice.rndb.Model.Comment

interface CommentOptionsClickListener {

    fun optionsMenuClicked(comment: Comment)
}