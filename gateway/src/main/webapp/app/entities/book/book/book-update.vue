<template>
    <div class="row justify-content-center">
        <div class="col-8">
            <form name="editForm" role="form" novalidate v-on:submit.prevent="save()" >
                <h2 id="gatewayApp.bookBook.home.createOrEditLabel" v-text="$t('gatewayApp.bookBook.home.createOrEditLabel')">Create or edit a Book</h2>
                <div>
                    <div class="form-group" v-if="book.id">
                        <label for="id" v-text="$t('global.field.id')">ID</label>
                        <input type="text" class="form-control" id="id" name="id"
                               v-model="book.id" readonly />
                    </div>
                    <div class="form-group">
                        <label class="form-control-label" v-text="$t('gatewayApp.bookBook.title')" for="book-title">Title</label>
                        <input type="text" class="form-control" name="title" id="book-title"
                            :class="{'valid': !$v.book.title.$invalid, 'invalid': $v.book.title.$invalid }" v-model="$v.book.title.$model" />
                    </div>
                    <div class="form-group">
                        <label class="form-control-label" v-text="$t('gatewayApp.bookBook.author')" for="book-author">Author</label>
                        <input type="text" class="form-control" name="author" id="book-author"
                            :class="{'valid': !$v.book.author.$invalid, 'invalid': $v.book.author.$invalid }" v-model="$v.book.author.$model" />
                    </div>
                    <div class="form-group">
                        <label class="form-control-label" v-text="$t('gatewayApp.bookBook.description')" for="book-description">Description</label>
                        <input type="text" class="form-control" name="description" id="book-description"
                            :class="{'valid': !$v.book.description.$invalid, 'invalid': $v.book.description.$invalid }" v-model="$v.book.description.$model" />
                    </div>
                </div>
                <div>
                    <button type="button" id="cancel-save" class="btn btn-secondary" v-on:click="previousState()">
                        <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
                    </button>
                    <button type="submit" id="save-entity" :disabled="$v.book.$invalid || isSaving" class="btn btn-primary">
                        <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>
<script lang="ts" src="./book-update.component.ts">
</script>
